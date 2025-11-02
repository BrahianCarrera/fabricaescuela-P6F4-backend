package com.fabricaescuela.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public Novedad registrarNovedad(Novedad novedad) {
        // Establecer fecha actual si no viene
        if (novedad.getFechaHora() == null) {
            novedad.setFechaHora(LocalDate.now());
        }
        return novedadRepository.save(novedad);
    }

    @Override
    public List<Novedad> obtenerTodasLasNovedades() {
        return novedadRepository.findAll();
    }

    @Override
    public Optional<Novedad> obtenerNovedadPorId(Integer id) {
        return novedadRepository.findById(id);
    }

    @Override
    public List<Novedad> findByIdPaqueteId(Integer idPaquete) {
        return novedadRepository.findByIdPaquete_Id(idPaquete);
    }

    @Override
    public Novedad actualizarNovedad(Integer id, Novedad novedad) {
        return novedadRepository.findById(id)
            .map(novedadExistente -> {
                if (novedad.getIdPaquete() != null) {
                    novedadExistente.setIdPaquete(novedad.getIdPaquete());
                }
                if (novedad.getTipoNovedad() != null) {
                    novedadExistente.setTipoNovedad(novedad.getTipoNovedad());
                }
                if (novedad.getDescripcion() != null) {
                    novedadExistente.setDescripcion(novedad.getDescripcion());
                }
                if (novedad.getFechaHora() != null) {
                    novedadExistente.setFechaHora(novedad.getFechaHora());
                }
                return novedadRepository.save(novedadExistente);
            })
            .orElse(null);
    }

    @Override
    public void eliminarNovedad(Integer id) {
        novedadRepository.deleteById(id);
    }
}
