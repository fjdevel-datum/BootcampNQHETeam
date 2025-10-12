package com.easycheck.domain.service;

import com.easycheck.application.dto.tarjetaDTO;

public interface IServiceTarjeta {


    
    tarjetaDTO crearTarjeta(tarjetaDTO dto) throws IllegalArgumentException;
}
