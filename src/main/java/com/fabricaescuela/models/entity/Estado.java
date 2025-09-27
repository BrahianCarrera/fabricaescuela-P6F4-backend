package com.fabricaescuela.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"Estados\"")
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idEstado\"", nullable = false)
    private Integer id;

    @Size(max = 30)
    @Column(name = "\"nombreEstado\"", length = 30)
    private String nombreEstado;

    @Size(max = 255)
    @Column(name = "\"descripcionEstado\"")
    private String descripcionEstado;

}