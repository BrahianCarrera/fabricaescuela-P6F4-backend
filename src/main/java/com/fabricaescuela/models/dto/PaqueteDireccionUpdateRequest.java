package com.fabricaescuela.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaqueteDireccionUpdateRequest(
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 30, message = "El destino no puede superar los 30 caracteres")
        String destino,
        @Size(max = 70, message = "El destinatario no puede superar los 70 caracteres")
        String destinatario
) {
}
