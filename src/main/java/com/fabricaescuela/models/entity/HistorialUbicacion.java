package com.fabricaescuela.models.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "historial_ubicaciones")
public class HistorialUbicacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHistorialUbicacion", nullable = false)
    private Integer id;

    @NotNull(message = "El paquete es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPaquete", nullable = false)
    private Paquete idPaquete;  // ⭐ Cambié "paquete" a "idPaquete" para consistencia

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(name = "ubicacion", columnDefinition = "TEXT")
    private String ubicacion;

    @Column(name = "fechaHora")
    private Instant fechaHora;
}