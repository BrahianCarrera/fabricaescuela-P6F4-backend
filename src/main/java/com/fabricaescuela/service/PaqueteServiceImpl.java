package com.fabricaescuela.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.entity.Estado;
import com.fabricaescuela.models.entity.HistorialEstado;
import com.fabricaescuela.models.entity.Paquete;
import com.fabricaescuela.repository.EstadoRepository;
import com.fabricaescuela.repository.HistorialEstadoRepository;
import com.fabricaescuela.repository.PaqueteRepository;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    private static final String ESTADO_EN_RUTA_TOKEN = "enruta";

    private final PaqueteRepository paqueteRepository;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final EstadoRepository estadoRepository;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository,
                              HistorialEstadoRepository historialEstadoRepository,
                              EstadoRepository estadoRepository) {
        this.paqueteRepository = paqueteRepository;
        this.historialEstadoRepository = historialEstadoRepository;
        this.estadoRepository = estadoRepository;
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
    
    // ⭐ NUEVOS MÉTODOS PARA VALIDACIÓN DE NOVEDADES ⭐
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Paquete> findById(Integer id) {
        return paqueteRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPaqueteEnTransito(Integer idPaquete) {
        return paqueteRepository.isPaqueteEnTransito(idPaquete);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Paquete> findPaquetesEnTransito() {
        return paqueteRepository.findPaquetesEnTransito();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Paquete> buscarPorCriterios(String codigoPaquete, java.time.LocalDate fechaRegistro, String nombreEstado) {
        return paqueteRepository.buscarPorCriterios(codigoPaquete, fechaRegistro, nombreEstado);
    }
    
    @Override
    @Transactional
    public PaqueteResponseDto actualizarEstado(String codigoPaquete, String nombreEstado) {
        // Buscar el paquete
        Paquete paquete = paqueteRepository.findByCodigoPaquete(codigoPaquete)
            .orElseThrow(() -> new IllegalArgumentException("Paquete no encontrado: " + codigoPaquete));

        // Normalizar el nombre del estado para comparación flexible
        String estadoBuscado = nombreEstado.trim().toLowerCase();
        
        // Buscar el estado en la base de datos
        Estado nuevoEstado = estadoRepository.findAll().stream()
            .filter(e -> e.getNombreEstado() != null && 
                        e.getNombreEstado().toLowerCase().equals(estadoBuscado))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Estado no encontrado: " + nombreEstado + ". Estados disponibles: " + 
                estadoRepository.findAll().stream()
                    .map(Estado::getNombreEstado)
                    .collect(Collectors.joining(", "))
            ));

        // Actualizar el estado del paquete
        paquete.setIdEstadoActual(nuevoEstado);
        Paquete paqueteActualizado = paqueteRepository.save(paquete);

        // Registrar en el historial de estados
        HistorialEstado historial = new HistorialEstado();
        historial.setIdPaquete(paquete);
        historial.setIdEstado(nuevoEstado);
        historial.setFechaHora(java.time.LocalDate.now());
        historialEstadoRepository.save(historial);

        return mapToDto(paqueteActualizado, nuevoEstado);
    }
}
