package com.easycheck.repository;

import com.easycheck.model.empresa;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class empresaRepository implements PanacheRepository<empresa> {
    
}
