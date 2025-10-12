package com.easycheck.domain.service;

import com.easycheck.application.dto.EmpleadoDTO;

public interface IServiceEmpleado {

    EmpleadoDTO crearEmpleado(EmpleadoDTO dto) throws IllegalArgumentException;
    
}
