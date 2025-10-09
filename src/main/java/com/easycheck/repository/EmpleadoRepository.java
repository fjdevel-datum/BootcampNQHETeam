package com.easycheck.repository;

import com.easycheck.model.empleado;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmpleadoRepository implements PanacheRepository<empleado>{
    
}
