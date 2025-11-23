package com.fabricaescuela.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "estados")
public class Estado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEstado", nullable = false)
    private Integer id;
    
    @NotBlank(message = "El nombre del estado es obligatorio")
    @Size(max = 30, message = "El nombre del estado no puede exceder 30 caracteres")
    @Column(name = "nombreEstado", length = 30)
    private String nombreEstado;
    
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    @Column(name = "descripcionEstado", length = 255)
    private String descripcionEstado;
}