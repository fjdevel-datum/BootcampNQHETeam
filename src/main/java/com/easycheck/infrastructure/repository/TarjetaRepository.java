package com.easycheck.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;

import com.easycheck.domain.model.tarjeta;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TarjetaRepository implements PanacheRepository<tarjeta> {
    
}
