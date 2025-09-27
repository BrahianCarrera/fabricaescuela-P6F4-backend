package com.fabricaescuela.service;

import com.fabricaescuela.models.dto.EstadoDto;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.entity.Paquete;
import com.fabricaescuela.repository.PaqueteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    private final PaqueteRepository paqueteRepository;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository) {
        this.paqueteRepository = paqueteRepository;
    }

    @Override
    public Optional<PaqueteResponseDto> consultarPorCodigo(String codigo) {
        Optional<Paquete> paqueteOpt = paqueteRepository.findByCodigo(codigo);

        return paqueteOpt.map(paquete ->
                PaqueteResponseDto.builder()
                        .id(paquete.getId())
                        .descripcion(paquete.getDescripcion())
                        .remitente(paquete.getRemitente())
                        .destinatario(paquete.getDestinatario())
                        .estado(EstadoDto.builder()
                                .id(null)
                                .nombre(paquete.getEstado().name()) // Usar .name() para convertir enum a String
                                .build())
                        .build()
        );
    }
}
