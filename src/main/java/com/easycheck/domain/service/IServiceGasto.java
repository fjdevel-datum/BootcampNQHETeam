package com.easycheck.domain.service;

import com.easycheck.application.dto.GastoDTO;

public interface IServiceGasto {

    GastoDTO crearGasto(GastoDTO dto) throws IllegalArgumentException;
    
    byte[] generarReporteExcel(Long empleadoId);
    
}
