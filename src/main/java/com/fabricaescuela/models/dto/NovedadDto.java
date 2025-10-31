package com.fabricaescuela.models.dto;

import java.time.LocalDateTime;

public record NovedadDto(
        String descripcion,
        LocalDateTime fecha,
        String registradoPor
) {}

