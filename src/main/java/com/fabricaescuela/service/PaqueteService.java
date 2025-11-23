package com.fabricaescuela.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.entity.Paquete;

public interface PaqueteService {
    List<PaqueteResponseDto> obtenerTodos();

    Optional<PaqueteResponseDto> consultarPorCodigo(String codigo);

    Optional<PaqueteResponseDto> consultarEnRutaPorCodigo(String codigo);

    PaqueteResponseDto actualizarDireccion(String codigo, PaqueteDireccionUpdateRequest request);
    
    // ⭐ NUEVOS MÉTODOS PARA VALIDACIÓN DE NOVEDADES ⭐
    
    /**
     * Busca un paquete por su ID
     */
    Optional<Paquete> findById(Integer id);
    
    /**
     * Verifica si un paquete está en tránsito
     */
    boolean isPaqueteEnTransito(Integer idPaquete);
    
    /**
     * Obtiene todos los paquetes en tránsito
     */
    List<Paquete> findPaquetesEnTransito();
    
    /**
     * Búsqueda flexible por múltiples criterios
     */
    List<Paquete> buscarPorCriterios(String codigoPaquete, LocalDate fechaRegistro, String nombreEstado);
}


