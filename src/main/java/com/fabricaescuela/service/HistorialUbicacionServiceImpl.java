package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.HistorialUbicacionRequest;
import com.fabricaescuela.models.dto.HistorialUbicacionResponse;
import com.fabricaescuela.models.entity.HistorialUbicacion;
import com.fabricaescuela.models.entity.Paquete;
import com.fabricaescuela.repository.HistorialUbicacionRepository;
import com.fabricaescuela.repository.PaqueteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialUbicacionServiceImpl implements HistorialUbicacionService {

    private final HistorialUbicacionRepository historialUbicacionRepository;
    private final PaqueteRepository paqueteRepository;

    public HistorialUbicacionServiceImpl(HistorialUbicacionRepository historialUbicacionRepository,
                                         PaqueteRepository paqueteRepository) {
        this.historialUbicacionRepository = historialUbicacionRepository;
        this.paqueteRepository = paqueteRepository;
    }

    @Override
    @Transactional
    public HistorialUbicacionResponse registrarUbicacion(String codigoPaquete, HistorialUbicacionRequest request) {
        Paquete paquete = paqueteRepository.findByCodigoPaquete(codigoPaquete)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));

        HistorialUbicacion historial = new HistorialUbicacion();
        historial.setPaquete(paquete);
        historial.setUbicacion(request.ubicacion());
        historial.setFechaHora(Instant.now());

        HistorialUbicacion guardado = historialUbicacionRepository.save(historial);
        return mapToResponse(paquete, guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialUbicacionResponse> consultarUbicacionesPorCodigo(String codigoPaquete) {
        Paquete paquete = paqueteRepository.findByCodigoPaquete(codigoPaquete)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));

        return historialUbicacionRepository.findByPaquete_IdOrderByFechaHoraDesc(paquete.getId()).stream()
                .map(historial -> mapToResponse(paquete, historial))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistorialUbicacionResponse> obtenerUltimaUbicacion(String codigoPaquete) {
        Paquete paquete = paqueteRepository.findByCodigoPaquete(codigoPaquete)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));

        return historialUbicacionRepository.findTopByPaquete_IdOrderByFechaHoraDesc(paquete.getId())
                .map(historial -> mapToResponse(paquete, historial));
    }

    private HistorialUbicacionResponse mapToResponse(Paquete paquete, HistorialUbicacion historial) {
        return HistorialUbicacionResponse.builder()
                .id(historial.getId())
                .codigoPaquete(paquete.getCodigoPaquete())
                .ubicacion(historial.getUbicacion())
                .fechaRegistro(historial.getFechaHora())
                .build();
    }
}
