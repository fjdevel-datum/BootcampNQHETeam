package com.easycheck.domain.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.ActividadDTO;
import com.easycheck.application.dto.ActividadListDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.empleado;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.EmpleadoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServiceActividadImp implements IServiceActividad {

    @Inject 
    ActividadRepository actividadRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    ///////////////////////////////////////////////////////
    // Actividades by EmpleadoId
    ///////////////////////////////////////////////////////

    @Override
    public List<ActividadListDTO> getActividadByEmpleadoId(Long empleadoId) {
        
        // Validación de parámetro
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
        }

        // Validamos si el empleado existe
        empleado empleado = empleadoRepository.findById(empleadoId);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado con ID " + empleadoId + " no encontrado");
        }

        System.out.println("🔍 Buscando actividades para empleado ID: " + empleadoId);

        // Actividades asociadas al empleado
        List<actividad> actividades = actividadRepository
            .find("empleado.empleadoId", empleadoId)
            .list();

        System.out.println("📊 Actividades encontradas: " + actividades.size());

        // Convertimos las entidades a DTOs
        return actividades.stream()
            .map(t -> new ActividadListDTO(
                t.getActividadId(),
                t.getNombre(),
                t.getEstado(),
                t.getEmpleado() != null ? t.getEmpleado().getEmpleadoId() : null
            ))
            .collect(Collectors.toList());
    }
    
    ///////////////////////////////////////////////////////
    // Crear Actividad
    ///////////////////////////////////////////////////////

    @Override
    @Transactional
    public ActividadDTO crearActividad(ActividadDTO dto) throws IllegalArgumentException {
        
        // Validaciones de entrada
        if (dto == null) {
            throw new IllegalArgumentException("El DTO de actividad no puede ser nulo");
        }

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la actividad es requerido");
        }

        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }

        if (dto.getFechaInicio() == null || dto.getFechaInicio().trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de inicio es requerida");
        }

        if (dto.getFechaFinal() == null || dto.getFechaFinal().trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha final es requerida");
        }

        // Validar que el empleado existe
        empleado empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + dto.getEmpleadoId());
        }

        // Parsear fechas
        Date fechaInicio;
        Date fechaFinal;
        try {
            LocalDate fechainicio = LocalDate.parse(dto.getFechaInicio(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate fechafinal = LocalDate.parse(dto.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Validar que la fecha final no sea anterior a la fecha de inicio
            if (fechafinal.isBefore(fechainicio)) {
                throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha de inicio");
            }

            fechaInicio = Date.from(fechainicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaFinal = Date.from(fechafinal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto. Use el formato yyyy-MM-dd (ej: 2025-01-15)");
        }

        // Crear nueva actividad
        actividad nuevaActividad = new actividad();
        nuevaActividad.setNombre(dto.getNombre());
        nuevaActividad.setFechaInicio(fechaInicio);
        nuevaActividad.setFechaFinal(fechaFinal);
        nuevaActividad.setEstado(dto.getEstado() != null ? dto.getEstado() : "Pendiente");
        nuevaActividad.setEmpleado(empleado);

        try {
            actividadRepository.persist(nuevaActividad);
            actividadRepository.flush(); // Forzar la persistencia inmediata
            System.out.println("✅ Actividad creada exitosamente con ID: " + nuevaActividad.getActividadId());
        } catch (Exception e) {
            System.err.println("❌ Error al persistir actividad: " + e.getMessage());
            throw new RuntimeException("Error al guardar la actividad en la base de datos", e);
        }

        // Crear y retornar DTO de respuesta
        ActividadDTO respuesta = new ActividadDTO(
            nuevaActividad.getActividadId(),
            nuevaActividad.getNombre(),
            dto.getFechaInicio(),
            dto.getFechaFinal(), 
            nuevaActividad.getEstado(),
            empleado.getEmpleadoId()
        );

        return respuesta;
    }
}