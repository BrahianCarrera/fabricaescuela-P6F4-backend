package com.fabricaescuela.models.entity;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "novedades")
@Schema(description = "Representa una novedad o incidencia registrada para un paquete")
public class Novedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idNovedad", nullable = false)
    @Schema(description = "ID único de la novedad", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPaquete")
    @Schema(description = "Paquete asociado a la novedad", requiredMode = Schema.RequiredMode.REQUIRED)
    private Paquete idPaquete;

    @Size(max = 30)
    @Column(name = "tipoNovedad", length = 30)
    @Schema(
        description = "Tipo de novedad", 
        example = "Retraso en entrega",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 30
    )
    private String tipoNovedad;

    @Size(max = 255)
    @Column(name = "descripcion")
    @Schema(
        description = "Descripción detallada de la novedad", 
        example = "Demora por condiciones climáticas adversas en la ruta",
        maxLength = 255
    )
    private String descripcion;

    @Column(name = "fechaHora")
    @Schema(
        description = "Fecha en que se registró la novedad", 
        example = "2025-11-02",
        type = "string",
        format = "date"
    )
    private LocalDate fechaHora;

}