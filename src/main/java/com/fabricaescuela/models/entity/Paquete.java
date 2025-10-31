package com.fabricaescuela.models.entity;


import java.time.LocalDate;

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
@Table(name = "paquetes")
public class Paquete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPaquete", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmpleadoResponsable")
    private Empleado idEmpleadoResponsable;

    @Size(max = 255)
    @Column(name = "codigoPaquete")
    private String codigoPaquete;

    @Size(max = 70)
    @Column(name = "remitente", length = 70)
    private String remitente;

    @Size(max = 70)
    @Column(name = "destinatario", length = 70)
    private String destinatario;

    @Column(name = "fechaRegistro")
    private LocalDate fechaRegistro;

    @Size(max = 30)
    @Column(name = "destino", length = 30)
    private String destino;

}