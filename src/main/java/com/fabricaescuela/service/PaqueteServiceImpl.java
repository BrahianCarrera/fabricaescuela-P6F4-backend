package com.fabricaescuela.service;

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
        Optional<Paquete> paqueteOpt = paqueteRepository.findByCodigoPaquete(codigo);

        return paqueteOpt.map(paquete ->
                PaqueteResponseDto.builder()
                        .id(paquete.getId())
                        .codigoPaquete(paquete.getCodigoPaquete())
                        .remitente(paquete.getRemitente())
                        .destinatario(paquete.getDestinatario())
                        .descripcion("...") // Si agregas descripción en la entidad, cambiar aquí
                        .build()
        );
    }
}
