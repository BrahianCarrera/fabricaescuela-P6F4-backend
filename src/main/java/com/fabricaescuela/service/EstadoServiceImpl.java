package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fabricaescuela.models.entity.Estado;
import com.fabricaescuela.repository.EstadoRepository;

@Service
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoServiceImpl(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public List<Estado> findAll() {
        return estadoRepository.findAll();
    }

    @Override
    public Optional<Estado> findById(Integer id) {
        return estadoRepository.findById(id);
    }

    @Override
    public Estado save(Estado estado) {
        return estadoRepository.save(estado);
    }

    @Override
    public void deleteById(Integer id) {
        estadoRepository.deleteById(id);
    }
}

