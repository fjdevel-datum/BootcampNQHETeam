package com.easycheck.domain.service;

import com.easycheck.application.dto.ActividadDTO;

public interface IServiceActividad {
    
    ActividadDTO crearActividad(ActividadDTO dto) throws IllegalArgumentException;
}
