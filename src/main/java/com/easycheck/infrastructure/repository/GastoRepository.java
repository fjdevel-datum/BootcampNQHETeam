package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.gasto;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GastoRepository implements PanacheRepository<gasto>  {
      public List<gasto> findByActividadId(Long actividadId) {
        return find("actividad.actividadId", actividadId).list();
    }
    
}
