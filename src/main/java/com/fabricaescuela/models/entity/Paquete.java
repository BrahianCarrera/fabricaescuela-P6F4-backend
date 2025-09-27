package com.fabricaescuela.models.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"Paquetes\"")
public class Paquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idPaquete\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idEmpleadoResponsable\"")
    private Empleado idEmpleadoResponsable;

    @Size(max = 255)
    @Column(name = "\"codigoPaquete\"")
    private String codigoPaquete;

    @Size(max = 70)
    @Column(name = "remitente", length = 70)
    private String remitente;

    @Size(max = 70)
    @Column(name = "destinatario", length = 70)
    private String destinatario;

    @Column(name = "\"fechaRegistro\"")
    private LocalDate fechaRegistro;

    @Size(max = 30)
    @Column(name = "destino", length = 30)
    private String destino;

}