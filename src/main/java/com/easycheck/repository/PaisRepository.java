package com.easycheck.repository;

import com.easycheck.model.pais;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaisRepository implements PanacheRepository<pais> {
    
}
