package com.easycheck.repository;

import com.easycheck.model.factura;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FacturaRepository implements PanacheRepository<factura> {
    
}
