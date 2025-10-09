package com.easycheck.service;

import com.easycheck.dto.GastoDTO;

public interface IServiceGasto {

    GastoDTO crearGasto(GastoDTO dto) throws IllegalArgumentException;
    
}
