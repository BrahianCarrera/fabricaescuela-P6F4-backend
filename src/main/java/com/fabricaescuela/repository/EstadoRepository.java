package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    // Puedes agregar consultas personalizadas si lo necesitas
    // Ejemplo:
    // List<Estado> findByNombreEstado(String nombreEstado);
}

