package com.easycheck.repository;

import com.easycheck.model.incidencia;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IncidenciaRepository implements PanacheRepository<incidencia>{
    
}
