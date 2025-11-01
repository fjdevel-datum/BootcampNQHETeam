package com.easycheck.domain.service;

import java.time.LocalDate;
import java.util.List;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.DetalleGastoDTO;

public interface IServiceGasto {

    GastoDTO crearGasto(GastoDTO dto) throws IllegalArgumentException;
    
    byte[] generarReporteExcel(Long empleadoId);

    

    List<DetalleGastoDTO> getDetalleGastos(Long empleadoId, LocalDate fechaInicio, LocalDate fechaFinal);
}
