package com.easycheck.service;

import com.easycheck.dto.RecursoAsignadoDTO;

public interface IServiceRecursoAsignado {
    
    RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException;
}
