package com.easycheck.domain.service;

import com.easycheck.application.dto.RecursoAsignadoDTO;

public interface IServiceRecursoAsignado {
    
    RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException;
}
