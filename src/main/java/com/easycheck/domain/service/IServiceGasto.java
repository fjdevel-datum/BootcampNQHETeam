package com.easycheck.domain.service;

import java.time.LocalDate;
import java.util.List;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.DetalleGastoDTO;
import com.easycheck.application.dto.DetalleGastoTarjetaDTO;

public interface IServiceGasto {

    GastoDTO crearGasto(GastoDTO dto) throws IllegalArgumentException;
    
    byte[] generarReporteExcel(Long empleadoId);

    List<GastoDTO> obtenerGastosPorActividad(Long actividadId);

    List<DetalleGastoDTO> getDetalleGastos(Long empleadoId, LocalDate fechaInicio, LocalDate fechaFinal);
    
    List<DetalleGastoTarjetaDTO> getDetalleGastosPorTarjeta(Long tarjetaId, LocalDate fechaInicio, LocalDate fechaFinal);


byte[] generarReporteExcelPorTarjeta(Long tarjetaId, LocalDate fechaInicio, LocalDate fechaFinal);}
