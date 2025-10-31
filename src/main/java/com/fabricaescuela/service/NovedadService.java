package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import com.fabricaescuela.models.entity.Novedad;

public interface NovedadService {
    // Crear nueva novedad (Principal para HU 2.2)
    Novedad registrarNovedad(Novedad novedad);
    
    // Consultar novedades
    List<Novedad> obtenerTodasLasNovedades();
    Optional<Novedad> obtenerNovedadPorId(Integer id);
    List<Novedad> findByIdPaqueteId(Integer idPaquete);
    
    // Actualizar y eliminar
    Novedad actualizarNovedad(Integer id, Novedad novedad);
    void eliminarNovedad(Integer id);
}