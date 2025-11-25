package com.fabricaescuela.models.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para actualizar una novedad existente.
 * Solo incluye los campos que se pueden modificar.
 */
@Getter
@Setter
public class ActualizarNovedadRequest {
    
    @NotBlank(message = "El tipo de novedad es obligatorio")
    @Size(max = 50, message = "El tipo de novedad no puede exceder 50 caracteres")
    private String tipoNovedad;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
    
    private LocalDate fechaHora;
}
