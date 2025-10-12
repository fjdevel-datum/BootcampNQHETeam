package com.easycheck.domain.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.easycheck.application.dto.RecursoAsignadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.TarjetaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ServiceRecursoAsignadoImp implements IServiceRecursoAsignado {
    
    @Inject
    RecursoAsignadoRepository recursoRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    TarjetaRepository tarjetaRepository;


    @Override
    public RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException{

        // validamos foraneas

        empleado empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if(empleado==null)
        {
            throw new IllegalArgumentException("Pais no encontrado");

        }
        tarjeta tarjeta = tarjetaRepository.findById(dto.getTarjetaId());
        if(tarjeta==null)
        {
            throw new IllegalArgumentException("Pais no encontrado");

        }

        //convertimos fecha
        Date fechaAsignacion;
        try {
            LocalDate localDate = LocalDate.parse(dto.getFechaAsignacion(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
             
            fechaAsignacion = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }


        //creamos

        recursoAsignado recurso = new recursoAsignado();
        recurso.setEmpleado(empleado);
        recurso.setTarjeta(tarjeta);
        recurso.setFechaAsignacion(fechaAsignacion);
        recurso.setMontoMaximo(dto.getMontoMaximo());
        recurso.setEstado(dto.getEstado());

        recursoRepository.persist(recurso);


        RecursoAsignadoDTO respuesta = new RecursoAsignadoDTO(
            recurso.getRecursoId(),
            empleado.getEmpleadoId(),
            tarjeta.getTarjetaId(),
            dto.getFechaAsignacion(),
            recurso.getMontoMaximo(),
            recurso.getEstado()
        );
        return respuesta;

    }


}
