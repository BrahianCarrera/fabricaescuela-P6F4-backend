package com.fabricaescuela.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HistorialUbicacionRequest(
        @NotBlank(message = "La ubicación es obligatoria")
        @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
        String ubicacion
) {
}
