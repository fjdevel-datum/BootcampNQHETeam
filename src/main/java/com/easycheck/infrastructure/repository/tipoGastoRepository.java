package com.easycheck.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;

import com.easycheck.domain.model.tipoGasto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class tipoGastoRepository implements PanacheRepository<tipoGasto> {
    
}
