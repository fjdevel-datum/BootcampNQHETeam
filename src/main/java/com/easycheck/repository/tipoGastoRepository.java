package com.easycheck.repository;

import jakarta.enterprise.context.ApplicationScoped;
import com.easycheck.model.tipoGasto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class tipoGastoRepository implements PanacheRepository<tipoGasto> {
    
}
