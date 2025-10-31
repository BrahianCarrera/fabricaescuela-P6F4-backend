package com.fabricaescuela.service;

import java.util.List;

import com.fabricaescuela.models.entity.Novedad;

public interface NovedadService {
    List<Novedad> findByIdPaqueteId(Integer idPaquete);
}