package com.easycheck.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public interface IServiceMoneda {

    
    Double convertirAMonedaBase(Long empleadoId, String monedaGastoCodigo, Double monto);
}