package com.fabricaescuela.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fabricaescuela.models.entity.Novedad;
import com.fabricaescuela.repository.NovedadRepository;

@Service
public class NovedadServiceImpl implements NovedadService {
    private final NovedadRepository novedadRepository;

    public NovedadServiceImpl(NovedadRepository novedadRepository) {
        this.novedadRepository = novedadRepository;
    }

    @Override
    public List<Novedad> findByIdPaqueteId(Integer idPaquete) {
        return novedadRepository.findByIdPaquete_Id(idPaquete);
    }
}
