package com.fabricaescuela.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paquete_id", nullable = false)
    private Paquete paquete;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private LocalDateTime fechaCambio;

    @ManyToOne
    @JoinColumn(name = "empleado_id")
    private User empleado; // el empleado que hizo el cambio
}
