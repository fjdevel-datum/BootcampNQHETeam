package com.easycheck.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;

import com.easycheck.domain.model.tipoIncidencia;

import io.quarkus.hibernate.orm.panache.PanacheRepository;  

@ApplicationScoped
public class tipoIncidenciaRepository  implements PanacheRepository<tipoIncidencia> {
    
}
