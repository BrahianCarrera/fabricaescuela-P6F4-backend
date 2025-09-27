package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByIdPaquete_Id(Long idPaquete);
}

