package com.easycheck.service;

import com.easycheck.dto.ActividadDTO;

public interface IServiceActividad {
    
    ActividadDTO crearActividad(ActividadDTO dto) throws IllegalArgumentException;
}
