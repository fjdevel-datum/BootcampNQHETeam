package com.easycheck.infrastructure.repository;

import com.easycheck.domain.model.tarjeta;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TarjetaRepository implements PanacheRepository<tarjeta> {

    public List<tarjeta> findByUsuarioId(Long usuarioId) {
        return list("usuario.usuarioId", usuarioId);
    }
}
