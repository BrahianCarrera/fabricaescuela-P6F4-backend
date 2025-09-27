package com.fabricaescuela.repository;

import com.fabricaescuela.models.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    // Aquí puedes agregar métodos de consulta personalizados si los necesitas
    // Ejemplo:
    // List<Empleado> findByNombreEmpleado(String nombreEmpleado);
}
