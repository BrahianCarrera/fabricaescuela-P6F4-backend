package com.fabricaescuela.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "novedades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Novedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "paquete_id", nullable = false)
    private Paquete paquete;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private User empleado; // quien registró la novedad
}
