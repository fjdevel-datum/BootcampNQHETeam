package com.easycheck.infrastructure.repository;

import java.util.List;
import com.easycheck.domain.model.tarjeta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TarjetaRepository implements PanacheRepository<tarjeta> {

    /**
     * Buscar tarjetas asignadas a un empleado a través de RecursoAsignado
     */
    public List<tarjeta> findByUsuarioId(Long empleadoId) {
        return find(
            "SELECT DISTINCT t FROM tarjeta t " +
            "JOIN t.recursosAsignados ra " +
            "WHERE ra.empleado.empleadoId = ?1",
            empleadoId
        ).list();
    }
}