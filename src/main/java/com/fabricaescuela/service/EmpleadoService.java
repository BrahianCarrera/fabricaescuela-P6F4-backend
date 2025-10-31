package com.fabricaescuela.service;

import java.util.List;
import java.util.Optional;

import com.fabricaescuela.models.entity.Empleado;

public interface EmpleadoService {
    List<Empleado> findAll();
    Optional<Empleado> findById(Integer id);
    Optional<Empleado> findByNumeroDocumento(Integer numeroDocumento);
    Empleado save(Empleado empleado);
    void deleteById(Integer id);
}
