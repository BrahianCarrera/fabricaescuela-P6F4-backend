package com.fabricaescuela.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para registrar una nueva ubicación de un paquete")
public record HistorialUbicacionRequest(
        @Schema(
            description = "Ubicación actual del paquete (coordenadas, dirección o descripción)",
            example = "Bodega Central Bogotá - Calle 100 #15-20",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 255
        )
        @NotBlank(message = "La ubicación es obligatoria")
        @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
        String ubicacion
) {
}
