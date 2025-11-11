package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.pais;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaisRepository implements PanacheRepository<pais> {
    
}
