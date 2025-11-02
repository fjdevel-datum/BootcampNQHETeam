package com.easycheck.domain.service;

import java.util.List;

import com.easycheck.application.dto.ActividadDTO;
import com.easycheck.application.dto.ActividadListDTO;

public interface IServiceActividad {
    
    ActividadDTO crearActividad(ActividadDTO dto) throws IllegalArgumentException;
    List<ActividadListDTO> getActividadByEmpleadoId(Long empleadoId);
    
}
