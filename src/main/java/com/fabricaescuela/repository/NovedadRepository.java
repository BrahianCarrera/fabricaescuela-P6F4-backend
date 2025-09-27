package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Novedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface NovedadRepository extends JpaRepository<Novedad, Integer> {

    List<Novedad> findByIdPaquete_Id(Integer paqueteId);
}

