package com.easycheck.infrastructure.repository;
import com.easycheck.domain.model.actividad;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActividadRepository implements PanacheRepository<actividad> {
}