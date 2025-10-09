package com.easycheck.service;

import com.easycheck.dto.tarjetaDTO;

public interface IServiceTarjeta {


    
    tarjetaDTO crearTarjeta(tarjetaDTO dto) throws IllegalArgumentException;
}
