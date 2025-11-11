package com.easycheck.domain.service;

import com.easycheck.application.dto.tarjetaDTO;
import java.util.List;

public interface IServiceTarjeta {
    
    tarjetaDTO crearTarjeta(tarjetaDTO dto) throws IllegalArgumentException;
    
    List<tarjetaDTO> obtenerTarjetasPorEmpleado(Long empleadoId) throws IllegalArgumentException;
    
    tarjetaDTO obtenerTarjetaPorId(Long tarjetaId) throws IllegalArgumentException;
    
    List<tarjetaDTO> listarTodasLasTarjetas();

    tarjetaDTO actualizarFechaExpiracion(Long tarjetaId, String fechaExpiracion) throws IllegalArgumentException;

}