package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.HistorialUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistorialUbicacionRepository extends JpaRepository<HistorialUbicacion, Integer> {
    List<HistorialUbicacion> findByPaquete_IdOrderByFechaHoraDesc(Integer idPaquete);

    Optional<HistorialUbicacion> findTopByPaquete_IdOrderByFechaHoraDesc(Integer idPaquete);
}
