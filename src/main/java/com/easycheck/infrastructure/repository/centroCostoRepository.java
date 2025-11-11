package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.centroCosto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class centroCostoRepository implements PanacheRepository<centroCosto> {
    
}
