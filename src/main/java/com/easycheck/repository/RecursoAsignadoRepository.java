package com.easycheck.repository;

import com.easycheck.model.recursoAsignado;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecursoAsignadoRepository implements PanacheRepository<recursoAsignado> {
    
}
