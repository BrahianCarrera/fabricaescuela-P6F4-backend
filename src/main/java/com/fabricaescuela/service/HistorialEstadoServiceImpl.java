package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fabricaescuela.models.entity.HistorialEstado;
import com.fabricaescuela.repository.HistorialEstadoRepository;

@Service
public class HistorialEstadoServiceImpl implements HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoServiceImpl(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Override
    public List<HistorialEstado> findAll() {
        return historialEstadoRepository.findAll();
    }

    @Override
    public Optional<HistorialEstado> findById(Integer id) {
        return historialEstadoRepository.findById(id);
    }

    @Override
    public List<HistorialEstado> findByIdPaquete(Integer idPaquete) {
        return historialEstadoRepository.findByIdPaquete_Id(idPaquete);
    }

    @Override
    public HistorialEstado save(HistorialEstado historialEstado) {
        return historialEstadoRepository.save(historialEstado);
    }

    @Override
    public void deleteById(Integer id) {
        historialEstadoRepository.deleteById(id);
    }
}
