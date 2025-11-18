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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "paquetes")  // ✅ Tabla "paquetes"
public class Paquete {      // ✅ Clase "Paquete"
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPaquete", nullable = false)
    private Integer id;

    @Column(name = "idEmpleadoResponsable")
    private Integer idEmpleadoResponsable;

    @NotBlank(message = "El código del paquete es obligatorio")
    @Size(max = 255)
    @Column(name = "codigoPaquete")
    private String codigoPaquete;

    @NotBlank(message = "El remitente es obligatorio")
    @Size(max = 70)
    @Column(name = "remitente", length = 70)
    private String remitente;

    @NotBlank(message = "El destinatario es obligatorio")
    @Size(max = 70)
    @Column(name = "destinatario", length = 70)
    private String destinatario;

    @NotNull(message = "La fecha de registro es obligatoria")
    @Column(name = "fechaRegistro")
    private LocalDate fechaRegistro;

    @NotBlank(message = "El destino es obligatorio")
    @Size(max = 30)
    @Column(name = "destino", length = 30)
    private String destino;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idEstadoActual")
    private Estado idEstadoActual;

    @Column(name = "idClienteRemitente")
    private Integer idClienteRemitente;

    @Column(name = "idClienteDestinatario")
    private Integer idClienteDestinatario;
}