package com.fabricaescuela.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fabricaescuela.models.entity.HistorialUbicacion;

@Repository
public interface HistorialUbicacionRepository extends JpaRepository<HistorialUbicacion, Integer> {
    List<HistorialUbicacion> findByIdPaquete_IdOrderByFechaHoraDesc(Integer idPaquete);

    Optional<HistorialUbicacion> findTopByIdPaquete_IdOrderByFechaHoraDesc(Integer idPaquete);
}
