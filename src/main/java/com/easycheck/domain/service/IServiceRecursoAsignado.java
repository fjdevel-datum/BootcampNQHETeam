package com.easycheck.domain.service;

import com.easycheck.application.dto.RecursoAsignadoDTO;
import java.util.List;

public interface IServiceRecursoAsignado {
    
    RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException;
    
    List<RecursoAsignadoDTO> obtenerRecursosPorEmpleado(Long empleadoId) throws IllegalArgumentException;
    
    RecursoAsignadoDTO obtenerRecursoPorId(Long recursoId) throws IllegalArgumentException;
    
    List<RecursoAsignadoDTO> listarTodosLosRecursos();
    
    void desactivarRecurso(Long recursoId) throws IllegalArgumentException;

    RecursoAsignadoDTO actualizarRecursoAsignado(Long recursoId, Double montoMaximo, String estado) throws IllegalArgumentException;
    
    RecursoAsignadoDTO obtenerRecursoPorTarjetaYEmpleado(Long tarjetaId, Long empleadoId) throws IllegalArgumentException;
}