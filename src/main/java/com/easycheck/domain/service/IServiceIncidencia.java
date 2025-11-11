package com.easycheck.domain.service;

import com.easycheck.application.dto.IncidenciaDTO;
import com.easycheck.application.dto.IncidenciaDetalleDTO;
import java.util.List;

public interface IServiceIncidencia  {
    IncidenciaDTO crearIncidencia(IncidenciaDTO dto) throws IllegalArgumentException;
    List<IncidenciaDetalleDTO> listarPorEmpleado(Long empleadoId);
    List<IncidenciaDetalleDTO> listarPorEmpresa(Long empresaId);
}
