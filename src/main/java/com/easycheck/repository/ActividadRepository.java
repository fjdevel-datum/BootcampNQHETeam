package com.easycheck.repository;
import com.easycheck.model.actividad;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActividadRepository implements PanacheRepository<actividad> {
}