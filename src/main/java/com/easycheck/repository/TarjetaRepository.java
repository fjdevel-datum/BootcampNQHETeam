package com.easycheck.repository;

import com.easycheck.model.tarjeta;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class TarjetaRepository implements PanacheRepository<tarjeta> {
    
}
