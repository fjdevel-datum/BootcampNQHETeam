package com.easycheck.domain.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.easycheck.application.dto.ActividadDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.empleado;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.EmpleadoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServiceActividadImp implements IServiceActividad{

    @Inject 
    ActividadRepository actividadRepository;

    @Inject
    EmpleadoRepository empleadoRepository;


    @Override
    public ActividadDTO crearActividad(ActividadDTO dto) throws IllegalArgumentException
    {
        empleado empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if(empleado==null)
        {
            throw new IllegalArgumentException("Pais no encontrado");

        }

        Date fechaInicio;
        Date fechaFinal;
        try {
            LocalDate fechainicio = LocalDate.parse(dto.getFechaInicio(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate fechafinal = LocalDate.parse(dto.getFechaFinal(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            fechaInicio = Date.from(fechainicio.atStartOfDay(ZoneId.systemDefault()).toInstant());            fechaInicio = Date.from(fechainicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaFinal = Date.from(fechafinal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }

        actividad nuevaActividad = new actividad();
        nuevaActividad.setNombre(dto.getNombre());
        nuevaActividad.setFechaInicio(fechaInicio);
        nuevaActividad.setFechaFinal(fechaFinal);
        nuevaActividad.setEstado(dto.getEstado());
        nuevaActividad.setEmpleado(empleado);
        

        actividadRepository.persist(nuevaActividad);

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
