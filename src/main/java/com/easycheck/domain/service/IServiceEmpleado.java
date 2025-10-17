package com.easycheck.domain.service;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.domain.model.empleado;

import java.util.List;

public interface IServiceEmpleado {
    
    /**
     * Crear un nuevo empleado
     */
    EmpleadoDTO crearEmpleado(EmpleadoDTO dto) throws IllegalArgumentException;
    
    /**
     * Actualizar un empleado existente
     */
    EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) throws IllegalArgumentException;
    
    /**
     * Obtener un empleado por su ID
     */
    EmpleadoDTO obtenerEmpleadoPorId(Long id) throws IllegalArgumentException;
    
    /**
     * Listar todos los empleados
     */
    List<EmpleadoDTO> listarTodosLosEmpleados();
    
    /**
     * Listar empleados por empresa
     */
    List<EmpleadoDTO> listarEmpleadosPorEmpresa(Long empresaId);
    
    /**
     * Listar empleados por centro de costo
     */
    List<EmpleadoDTO> listarEmpleadosPorCentroCosto(Long centroId);
    
    /**
     * Buscar empleado por UID de Firebase (retorna entidad)
     */
    empleado buscarPorUid(String uid);
    
    /**
     * Buscar empleado por UID de Firebase (retorna DTO)
     */
    EmpleadoDTO buscarPorUidDTO(String uid);
    
    /**
     * Buscar empleado por documento de identidad
     */
    EmpleadoDTO buscarPorDocumento(String documentoIdentidad);
    
    /**
     * Eliminar un empleado
     */
    void eliminarEmpleado(Long id) throws IllegalArgumentException;
    
    /**
     * Contar empleados por empresa
     */
    long contarEmpleadosPorEmpresa(Long empresaId);
    
    /**
     * Verificar si existe un empleado con el documento dado
     */
    boolean existeEmpleadoConDocumento(String documentoIdentidad);
    
    /**
     * Verificar si existe un empleado con el UID dado
     */
    boolean existeEmpleadoConUid(String uid);
}