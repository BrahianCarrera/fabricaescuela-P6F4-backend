package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import com.fabricaescuela.models.entity.HistorialEstado;

public interface HistorialEstadoService {

    List<HistorialEstado> findAll();

    Optional<HistorialEstado> findById(Integer id);

    List<HistorialEstado> findByIdPaquete(Integer idPaquete);

    HistorialEstado save(HistorialEstado historialEstado);

    void deleteById(Integer id);
}
