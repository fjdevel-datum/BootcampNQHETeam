package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.incidencia;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IncidenciaRepository implements PanacheRepository<incidencia>{
    
}
