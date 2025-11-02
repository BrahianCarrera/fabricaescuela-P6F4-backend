package com.fabricaescuela.service;

import com.fabricaescuela.models.entity.Empleado;
import com.fabricaescuela.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public List<Empleado> findAll() {
        return empleadoRepository.findAll();
    }

    @Override
    public Optional<Empleado> findById(Integer id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public Optional<Empleado> findByNumeroDocumento(Integer numeroDocumento) {
        return empleadoRepository.findByNumeroDocumento(numeroDocumento);
    }

    @Override
    public Empleado save(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public void deleteById(Integer id) {
        empleadoRepository.deleteById(id);
    }
}