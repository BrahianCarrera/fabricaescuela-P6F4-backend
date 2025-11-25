package com.fabricaescuela.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fabricaescuela.models.entity.Paquete;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    
    // Ya existe
    Optional<Paquete> findByCodigoPaquete(String codigoPaquete);
    
    // ⭐ AGREGAR ESTOS MÉTODOS ⭐
    
    // Buscar por fecha de registro
    List<Paquete> findByFechaRegistro(LocalDate fechaRegistro);
    
    // Buscar paquetes en tránsito
    @Query("SELECT p FROM Paquete p WHERE LOWER(p.idEstadoActual.nombreEstado) = 'en transito'")
    List<Paquete> findPaquetesEnTransito();
    
    // Verificar si un paquete está en tránsito
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Paquete p WHERE p.id = :idPaquete " +
           "AND LOWER(p.idEstadoActual.nombreEstado) = 'en transito'")
    boolean isPaqueteEnTransito(@Param("idPaquete") Integer idPaquete);
    
    // Buscar por múltiples criterios (flexible)
    @Query("SELECT p FROM Paquete p WHERE " +
           "(:codigoPaquete IS NULL OR p.codigoPaquete = :codigoPaquete) AND " +
           "(:fechaRegistro IS NULL OR p.fechaRegistro = :fechaRegistro) AND " +
           "(:nombreEstado IS NULL OR p.idEstadoActual.nombreEstado = :nombreEstado)")
    List<Paquete> buscarPorCriterios(
        @Param("codigoPaquete") String codigoPaquete,
        @Param("fechaRegistro") LocalDate fechaRegistro,
        @Param("nombreEstado") String nombreEstado
    );
}