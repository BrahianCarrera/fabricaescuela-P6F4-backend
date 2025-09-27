package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.HistorialUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialUbicacionRepository extends JpaRepository<HistorialUbicacion, Integer> {
    // Métodos personalizados si los necesitas
    // Ejemplo: List<HistorialUbicacion> findByUbicacion(String ubicacion);
}
