package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.entity.Estado;
import com.fabricaescuela.models.entity.HistorialEstado;
import com.fabricaescuela.models.entity.Paquete;
import com.fabricaescuela.repository.HistorialEstadoRepository;
import com.fabricaescuela.repository.PaqueteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    private static final String ESTADO_EN_RUTA_TOKEN = "enruta";

    private final PaqueteRepository paqueteRepository;
    private final HistorialEstadoRepository historialEstadoRepository;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository,
                              HistorialEstadoRepository historialEstadoRepository) {
        this.paqueteRepository = paqueteRepository;
        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteResponseDto> obtenerTodos() {
        return paqueteRepository.findAll().stream()
                .map(paquete -> mapToDto(paquete, obtenerEstadoActual(paquete)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaqueteResponseDto> consultarPorCodigo(String codigo) {
        return paqueteRepository.findByCodigoPaquete(codigo)
                .map(paquete -> mapToDto(paquete, obtenerEstadoActual(paquete)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaqueteResponseDto> consultarEnRutaPorCodigo(String codigo) {
        return paqueteRepository.findByCodigoPaquete(codigo)
                .flatMap(paquete -> {
                    Estado estadoActual = obtenerEstadoActual(paquete);
                    if (!esEstadoEnRuta(estadoActual)) {
                        return Optional.empty();
                    }
                    return Optional.of(mapToDto(paquete, estadoActual));
                });
    }

    @Override
    @Transactional
    public PaqueteResponseDto actualizarDireccion(String codigo, PaqueteDireccionUpdateRequest request) {
        Paquete paquete = paqueteRepository.findByCodigoPaquete(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));

        Estado estadoActual = obtenerEstadoActual(paquete);
        if (!esEstadoEnRuta(estadoActual)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El paquete no está en ruta");
        }

        paquete.setDestino(request.destino());
        if (request.destinatario() != null) {
            paquete.setDestinatario(request.destinatario());
        }

        Paquete actualizado = paqueteRepository.save(paquete);
        return mapToDto(actualizado, estadoActual);
    }

    private Estado obtenerEstadoActual(Paquete paquete) {
        if (paquete.getId() == null) {
            return null;
        }

        return historialEstadoRepository.findTopByIdPaquete_IdOrderByFechaHoraDesc(paquete.getId())
                .map(HistorialEstado::getIdEstado)
                .orElse(null);
    }

    private PaqueteResponseDto mapToDto(Paquete paquete, Estado estadoActual) {
        return PaqueteResponseDto.builder()
                .id(paquete.getId())
                .codigoPaquete(paquete.getCodigoPaquete())
                .remitente(paquete.getRemitente())
                .destinatario(paquete.getDestinatario())
                .destino(paquete.getDestino())
                .estadoActual(estadoActual != null ? estadoActual.getNombreEstado() : null)
                .build();
    }

    private boolean esEstadoEnRuta(Estado estado) {
        if (estado == null || estado.getNombreEstado() == null) {
            return false;
        }

    String normalizado = Normalizer.normalize(estado.getNombreEstado(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT)
        .replaceAll("\\s", "");
        return normalizado.contains(ESTADO_EN_RUTA_TOKEN);
    }
}
