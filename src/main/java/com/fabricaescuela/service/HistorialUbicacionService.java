package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.HistorialUbicacionRequest;
import com.fabricaescuela.models.dto.HistorialUbicacionResponse;

import java.util.List;
import java.util.Optional;

public interface HistorialUbicacionService {

    HistorialUbicacionResponse registrarUbicacion(String codigoPaquete, HistorialUbicacionRequest request);

    List<HistorialUbicacionResponse> consultarUbicacionesPorCodigo(String codigoPaquete);

    Optional<HistorialUbicacionResponse> obtenerUltimaUbicacion(String codigoPaquete);
}
