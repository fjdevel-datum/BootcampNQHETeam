package com.easycheck.repository;

import jakarta.enterprise.context.ApplicationScoped;
import com.easycheck.model.tipoIncidencia;
import io.quarkus.hibernate.orm.panache.PanacheRepository;  

@ApplicationScoped
public class tipoIncidenciaRepository  implements PanacheRepository<tipoIncidencia> {
    
}
