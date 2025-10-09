package com.easycheck.repository;

import com.easycheck.model.tipoTarjeta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class tipoTarjetaRepository implements PanacheRepository<tipoTarjeta> {
    
}
