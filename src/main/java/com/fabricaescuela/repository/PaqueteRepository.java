package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Ejemplo de documentación sugerida
/**
 * Repositorio para la entidad Paquete
 */
public interface PaqueteRepository extends JpaRepository<Paquete, Long> {
    /**
     * Busca un paquete por su código único
     * @param codigo Código del paquete a buscar
     * @return Optional conteniendo el paquete si existe
     */
    Optional<Paquete> findByCodigo(String codigo);
}