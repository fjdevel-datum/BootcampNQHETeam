package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.factura;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FacturaRepository implements PanacheRepository<factura> {
    
}
