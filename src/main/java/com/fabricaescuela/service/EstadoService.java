package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import com.fabricaescuela.models.entity.Estado;

public interface EstadoService {

    List<Estado> findAll();

    Optional<Estado> findById(Integer id);

    Estado save(Estado estado);

    void deleteById(Integer id);
}

