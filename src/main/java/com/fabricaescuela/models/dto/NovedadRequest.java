package com.fabricaescuela.models.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NovedadRequest {
    
    @NotNull(message = "El ID del paquete es obligatorio")
    private Integer idPaquete;
    
    @NotBlank(message = "El tipo de novedad es obligatorio")
    @Size(max = 30, message = "El tipo de novedad no puede exceder 30 caracteres")
    private String tipoNovedad;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaHora;
}
