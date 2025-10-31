package com.fabricaescuela.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fabricaescuela.models.entity.HistorialEstado;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Integer> {
    List<HistorialEstado> findByIdPaquete_Id(Integer idPaquete);
}

