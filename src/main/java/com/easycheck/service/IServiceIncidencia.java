package com.easycheck.service;

import com.easycheck.dto.IncidenciaDTO;

public interface IServiceIncidencia  {
    IncidenciaDTO crearIncidencia(IncidenciaDTO dto) throws IllegalArgumentException;
}
