package com.easycheck.domain.service;

import com.easycheck.application.dto.FacturaDTO;

public interface IServiceFactura {
    FacturaDTO crearFactura(FacturaDTO dto) throws IllegalArgumentException;
}
