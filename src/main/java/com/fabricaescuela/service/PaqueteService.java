package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;

import java.util.List;
import java.util.Optional;

public interface PaqueteService {
    List<PaqueteResponseDto> obtenerTodos();

    Optional<PaqueteResponseDto> consultarPorCodigo(String codigo);

    Optional<PaqueteResponseDto> consultarEnRutaPorCodigo(String codigo);

    PaqueteResponseDto actualizarDireccion(String codigo, PaqueteDireccionUpdateRequest request);
}


