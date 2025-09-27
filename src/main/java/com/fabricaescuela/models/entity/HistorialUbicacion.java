package com.fabricaescuela.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "\"HistorialUbicaciones\"")
public class HistorialUbicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idHistorialUbicacion\"", nullable = false)
    private Integer id;

    @Column(name = "ubicacion", length = Integer.MAX_VALUE)
    private String ubicacion;

    @Column(name = "\"fechaHora\"")
    private Instant fechaHora;

}