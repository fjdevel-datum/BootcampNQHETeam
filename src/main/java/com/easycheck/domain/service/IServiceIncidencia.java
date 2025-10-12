package com.easycheck.domain.service;

import com.easycheck.application.dto.IncidenciaDTO;

public interface IServiceIncidencia  {
    IncidenciaDTO crearIncidencia(IncidenciaDTO dto) throws IllegalArgumentException;
}
