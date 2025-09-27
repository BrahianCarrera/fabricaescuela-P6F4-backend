package com.fabricaescuela.models.entity;

import jakarta.persistence.*; // Agregar esta importación
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paquetes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //private String codigo;   // Identificador único del paquete

    private String remitente;

    private String descripcion;

    private String destinatario;

    private String direccionDestino;

    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(unique = true)
    @Size(min = 3, max = 50)
    private String codigo;   // Identificador único del paquete

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialEstado> historialEstados;

    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Novedad> novedades;
}

