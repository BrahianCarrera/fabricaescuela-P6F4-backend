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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"HistorialEstados\"")
public class HistorialEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idHistoriaEstadol\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idEmpleado\"")
    private Empleado idEmpleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idPaquete\"")
    private Paquete idPaquete; // El nombre debe coincidir con el método del repositorio

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idEstado\"")
    private Estado idEstado;

    @Column(name = "\"fechaHora\"")
    private LocalDate fechaHora;

}