package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaqueteRepository extends JpaRepository<Paquete, Integer> {
    Optional<Paquete> findByCodigoPaquete(String codigoPaquete);
}
