package com.easycheck.domain.service;

import com.easycheck.application.dto.RecursoAsignadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.TarjetaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceRecursoAsignadoImp implements IServiceRecursoAsignado {

    @Inject
    RecursoAsignadoRepository recursoAsignadoRepository;

    @Inject
    TarjetaRepository tarjetaRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Override
    @Transactional
    public RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException {
        
        System.out.println("Creando recurso asignado...");
        
        // Validaciones
        if (dto.getTarjetaId() == null) {
            throw new IllegalArgumentException("El ID de la tarjeta es requerido");
        }

        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }

        // Verificar que la tarjeta existe
        tarjeta tarjeta = tarjetaRepository.findById(dto.getTarjetaId());
        if (tarjeta == null) {
            throw new IllegalArgumentException("Tarjeta no encontrada con ID: " + dto.getTarjetaId());
        }

        // Verificar que el empleado existe
        empleado empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + dto.getEmpleadoId());
        }

        // Verificar que no exista ya una asignación activa de esta tarjeta al empleado
        List<recursoAsignado> asignacionesExistentes = recursoAsignadoRepository
            .find("tarjeta.tarjetaId = ?1 and empleado.empleadoId = ?2 and estado = ?3", 
                  dto.getTarjetaId(), dto.getEmpleadoId(), "Activo")
            .list();

        if (!asignacionesExistentes.isEmpty()) {
            throw new IllegalArgumentException("Esta tarjeta ya está asignada activamente a este empleado");
        }

        System.out.println("Asignando tarjeta " + tarjeta.getNumeroTarjeta() + " al empleado " + empleado.getNombres());

        // Parsear fecha de asignación
        Date fechaAsignacion = new Date(); // Fecha actual por defecto
        if (dto.getFechaAsignacion() != null && !dto.getFechaAsignacion().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fechaAsignacion = sdf.parse(dto.getFechaAsignacion());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Formato de fecha incorrecto. Use yyyy-MM-dd");
            }
        }

        // Crear nuevo recurso asignado
        recursoAsignado nuevoRecurso = new recursoAsignado();
        nuevoRecurso.setTarjeta(tarjeta);
        nuevoRecurso.setEmpleado(empleado);
        nuevoRecurso.setFechaAsignacion(fechaAsignacion);
        nuevoRecurso.setMontoMaximo(dto.getMontoMaximo());
        nuevoRecurso.setEstado(dto.getEstado() != null ? dto.getEstado() : "Activo");

        try {
            recursoAsignadoRepository.persist(nuevoRecurso);
            recursoAsignadoRepository.flush();
            System.out.println("Recurso asignado creado con ID: " + nuevoRecurso.getRecursoId());
        } catch (Exception e) {
            System.err.println("Error al persistir recurso: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el recurso asignado en la base de datos", e);
        }

        // Retornar DTO
        return mapToDTO(nuevoRecurso);
    }

    @Override
    public List<RecursoAsignadoDTO> obtenerRecursosPorEmpleado(Long empleadoId) throws IllegalArgumentException {
        
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
        }

        // Verificar que el empleado existe
        empleado empleado = empleadoRepository.findById(empleadoId);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + empleadoId);
        }

        System.out.println("🔍 Buscando recursos para empleado: " + empleado.getNombres() + " " + empleado.getApellidos());

        // Obtener recursos del empleado
        List<recursoAsignado> recursos = recursoAsignadoRepository
            .find("empleado.empleadoId", empleadoId)
            .list();

        System.out.println("Recursos encontrados: " + recursos.size());

        // Mapear a DTO
        return recursos.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public RecursoAsignadoDTO obtenerRecursoPorId(Long recursoId) throws IllegalArgumentException {
        
        if (recursoId == null) {
            throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
        }

        recursoAsignado recurso = recursoAsignadoRepository.findById(recursoId);
        if (recurso == null) {
            throw new IllegalArgumentException("Recurso no encontrado con ID: " + recursoId);
        }

        return mapToDTO(recurso);
    }

    @Override
    public List<RecursoAsignadoDTO> listarTodosLosRecursos() {
        
        System.out.println("Listando todos los recursos asignados");
        
        List<recursoAsignado> recursos = recursoAsignadoRepository.listAll();
        
        System.out.println("Total de recursos: " + recursos.size());
        
        return recursos.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void desactivarRecurso(Long recursoId) throws IllegalArgumentException {
        
        if (recursoId == null) {
            throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
        }

        recursoAsignado recurso = recursoAsignadoRepository.findById(recursoId);
        if (recurso == null) {
            throw new IllegalArgumentException("Recurso no encontrado con ID: " + recursoId);
        }

        System.out.println("Desactivando recurso ID: " + recursoId);

        recurso.setEstado("Inactivo");
        
        try {
            recursoAsignadoRepository.persist(recurso);
            System.out.println("Recurso desactivado");
        } catch (Exception e) {
            System.err.println("Error al desactivar recurso: " + e.getMessage());
            throw new RuntimeException("Error al desactivar el recurso", e);
        }
    }

    /**
     * Mapea una entidad recursoAsignado a DTO
     */
    private RecursoAsignadoDTO mapToDTO(recursoAsignado recurso) {
        return new RecursoAsignadoDTO(
            recurso.getRecursoId(),
            recurso.getEmpleado() != null ? recurso.getEmpleado().getEmpleadoId() : null,
            recurso.getTarjeta() != null ? recurso.getTarjeta().getTarjetaId() : null,
            recurso.getFechaAsignacion() != null ? 
                new SimpleDateFormat("yyyy-MM-dd").format(recurso.getFechaAsignacion()) : null,
            recurso.getMontoMaximo(),
            recurso.getEstado()
        );
    }
}