package com.fabricaescuela.models.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaqueteResponseDto {
    private Long id;
    private String descripcion;
    private String remitente;
    private String destinatario;
    private EstadoDto estado;
}

