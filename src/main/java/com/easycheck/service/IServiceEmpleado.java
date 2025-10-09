package com.easycheck.service;

import com.easycheck.dto.EmpleadoDTO;

public interface IServiceEmpleado {

    EmpleadoDTO crearEmpleado(EmpleadoDTO dto) throws IllegalArgumentException;
    
}
