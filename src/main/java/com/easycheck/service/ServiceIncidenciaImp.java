package com.easycheck.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.easycheck.dto.IncidenciaDTO;
import com.easycheck.model.empleado;
import com.easycheck.model.incidencia;
import com.easycheck.model.recursoAsignado;
import com.easycheck.model.tipoIncidencia;
import com.easycheck.repository.EmpleadoRepository;
import com.easycheck.repository.IncidenciaRepository;
import com.easycheck.repository.RecursoAsignadoRepository;
import com.easycheck.repository.tipoIncidenciaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServiceIncidenciaImp implements IServiceIncidencia {
    
    @Inject
    IncidenciaRepository incidenciaRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    tipoIncidenciaRepository tipoIncidenciaRepository;

    @Inject
    RecursoAsignadoRepository recursoRepository;

    @Override
    @Transactional
    public IncidenciaDTO crearIncidencia(IncidenciaDTO dto) throws IllegalArgumentException
    {

        //validamos foraneas
        empleado  empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if(empleado==null)
        {
            throw new IllegalArgumentException("Empleado no encontrado");
       
        }
        tipoIncidencia tipo  = tipoIncidenciaRepository.findById(dto.getTipoIncidenciaId());
        if(tipo==null)
        {
            throw new IllegalArgumentException("Tipo de Incidencia no existe");
        }
        recursoAsignado recurso = recursoRepository.findById(dto.getRecursoId());
        if(recurso==null)
        {
            throw new IllegalArgumentException("Recurso asignado no encontrado");
            
        }

        Date fechaIncidencia;
        try
        {
            LocalDate localDate = LocalDate.parse(dto.getFechaIncidencia(), DateTimeFormatter.ofPattern("yyy-MM-dd"));
            fechaIncidencia = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }


        incidencia nuevIncidencia = new incidencia();
        nuevIncidencia.setEmpleado(empleado);
        nuevIncidencia.setTipoIncidencia(tipo);
        nuevIncidencia.setRecursoAsignado(recurso);
        nuevIncidencia.setFechaIncidencia(fechaIncidencia);
        nuevIncidencia.setDescripcion(dto.getDescripcion());

        incidenciaRepository.persist(nuevIncidencia);

        IncidenciaDTO respuesta = new IncidenciaDTO(
            nuevIncidencia.getIncidenciaId(),
            empleado.getEmpleadoId(),
            tipo.getTipoIncidenciaId(),
            recurso.getRecursoId(),
            dto.getFechaIncidencia(),
            nuevIncidencia.getDescripcion()
        );
        return respuesta;

    }


}
