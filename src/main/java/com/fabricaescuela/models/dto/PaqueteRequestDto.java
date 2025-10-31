package com.fabricaescuela.models.dto;

import java.time.LocalDateTime;

public record PaqueteRequestDto(
        String remitente,
        String destinatario,
        String destino,
        Double peso,
        LocalDateTime fechaRegistro
) {}