package com.fabricaescuela.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"Novedades\"")
public class Novedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idNovedad\"", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idPaquete\"")
    private Paquete idPaquete;

    @Size(max = 30)
    @Column(name = "\"tipoNovedad\"", length = 30)
    private String tipoNovedad;

    @Size(max = 255)
    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "\"fechaHora\"")
    private LocalDate fechaHora;

}