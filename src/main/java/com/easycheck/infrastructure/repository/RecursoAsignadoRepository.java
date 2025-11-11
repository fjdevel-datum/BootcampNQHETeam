package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.recursoAsignado;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RecursoAsignadoRepository implements PanacheRepository<recursoAsignado> {
    
}
