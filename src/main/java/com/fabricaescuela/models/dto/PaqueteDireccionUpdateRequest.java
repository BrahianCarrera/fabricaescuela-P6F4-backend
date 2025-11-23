package com.fabricaescuela.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para actualizar la dirección de un paquete en ruta")
public record PaqueteDireccionUpdateRequest(
        @Schema(
            description = "Nueva dirección de destino del paquete",
            example = "Cali, Valle del Cauca",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 30
        )
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 30, message = "El destino no puede superar los 30 caracteres")
        String destino,
        
        @Schema(
            description = "Nombre del nuevo destinatario (opcional)",
            example = "Juan Pérez García",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 70
        )
        @Size(max = 70, message = "El destinatario no puede superar los 70 caracteres")
        String destinatario
) {
}
