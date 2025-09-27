package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NovedadRepository extends JpaRepository<Novedad, Long> {

    List<Novedad> findByPaquete_Id(Long paqueteId);
}

