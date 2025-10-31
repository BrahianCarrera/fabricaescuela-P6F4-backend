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
@Table(name = "novedades")
public class Novedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idNovedad", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPaquete")
    private Paquete idPaquete;

    @Size(max = 30)
    @Column(name = "tipoNovedad", length = 30)
    private String tipoNovedad;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fechaHora")
    private LocalDate fechaHora;

}