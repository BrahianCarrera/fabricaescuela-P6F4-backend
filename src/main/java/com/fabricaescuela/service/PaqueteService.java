package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.PaqueteResponseDto;

import java.util.Optional;

public interface PaqueteService {
    Optional<PaqueteResponseDto> consultarPorCodigo(String codigo);
}


