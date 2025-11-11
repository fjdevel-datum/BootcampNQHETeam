package com.easycheck.domain.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.IncidenciaDTO;
import com.easycheck.application.dto.IncidenciaDetalleDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.incidencia;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tipoIncidencia;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.IncidenciaRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.tipoIncidenciaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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

    @Inject
    EntityManager em;

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

     @Override
    public List<IncidenciaDetalleDTO> listarPorEmpleado(Long empleadoId) {
        try {
            System.out.println("🔍 Buscando incidencias para empleado ID: " + empleadoId);
            
            if (empleadoId == null) {
                throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
            }
            @SuppressWarnings("unchecked")
            List<Object[]> results = em.createNativeQuery("""
                SELECT 
                    i.incidenciaid,
                    i.empleadoid,
                    e.nombres || ' ' || e.apellidos AS nombreEmpleado,
                    i.tipoincidenciaid,
                    ti.descripcion AS tipoIncidenciaNombre,
                    i.recursoid,
                    t.numerotarjeta,
                    TO_CHAR(i.fechaincidencia, 'YYYY-MM-DD') AS fechaIncidencia,
                    i.descripcion
                FROM incidencia i
                JOIN empleado e ON i.empleadoid = e.empleadoid
                JOIN tipoincidencia ti ON i.tipoincidenciaid = ti.tipoincidenciaid
                JOIN recursoasignado r ON i.recursoid = r.recursoid
                JOIN tarjeta t ON r.tarjetaid = t.tarjetaid
                WHERE i.empleadoid = :empleadoId
                ORDER BY i.fechaincidencia DESC
            """)
            .setParameter("empleadoId", empleadoId)
            .getResultList();

            List<IncidenciaDetalleDTO> incidencias = results.stream()
                .map(row -> new IncidenciaDetalleDTO(
                    ((Number) row[0]).longValue(),     // incidenciaId
                    ((Number) row[1]).longValue(),     // empleadoId
                    (String) row[2],                   // nombreEmpleado
                    ((Number) row[3]).longValue(),     // tipoIncidenciaId
                    (String) row[4],                   // tipoIncidenciaNombre
                    ((Number) row[5]).longValue(),     // recursoId
                    (String) row[6],                   // numeroTarjeta
                    (String) row[7],                   // fechaIncidencia
                    (String) row[8]                    // descripcion
                ))
                .collect(Collectors.toList());

            System.out.println("Encontradas " + incidencias.size() + " incidencias");
            
            return incidencias;

        } catch (Exception e) {
            System.err.println("Error al listar incidencias: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al listar incidencias: " + e.getMessage(), e);
        }
    }

// ServiceIncidenciaImp.java - Método sin email
@Override
public List<IncidenciaDetalleDTO> listarPorEmpresa(Long empresaId) {
    try {
        System.out.println("🔍 Buscando incidencias para empresa ID: " + empresaId);
        
        if (empresaId == null) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser nulo");
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery("""
            SELECT 
                i.incidenciaid,
                i.empleadoid,
                e.nombres || ' ' || e.apellidos AS nombreEmpleado,
                i.tipoincidenciaid,
                ti.descripcion AS tipoIncidenciaNombre,
                i.recursoid,
                t.numerotarjeta,
                TO_CHAR(i.fechaincidencia, 'YYYY-MM-DD') AS fechaIncidencia,
                i.descripcion
            FROM incidencia i
            JOIN empleado e ON i.empleadoid = e.empleadoid
            JOIN tipoincidencia ti ON i.tipoincidenciaid = ti.tipoincidenciaid
            JOIN recursoasignado r ON i.recursoid = r.recursoid
            JOIN tarjeta t ON r.tarjetaid = t.tarjetaid
            WHERE e.empresaid = :empresaId
            ORDER BY i.fechaincidencia DESC
        """)
        .setParameter("empresaId", empresaId)
        .getResultList();

        List<IncidenciaDetalleDTO> incidencias = results.stream()
            .map(row -> new IncidenciaDetalleDTO(
                ((Number) row[0]).longValue(),     // incidenciaId
                ((Number) row[1]).longValue(),     // empleadoId
                (String) row[2],                   // nombreEmpleado
                ((Number) row[3]).longValue(),     // tipoIncidenciaId
                (String) row[4],                   // tipoIncidenciaNombre
                ((Number) row[5]).longValue(),     // recursoId
                (String) row[6],                   // numeroTarjeta
                (String) row[7],                   // fechaIncidencia
                (String) row[8]                    // descripcion
            ))
            .collect(Collectors.toList());

        System.out.println("✅ Encontradas " + incidencias.size() + " incidencias para la empresa");
        
        return incidencias;

    } catch (Exception e) {
        System.err.println("❌ Error al listar incidencias por empresa: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error al listar incidencias por empresa: " + e.getMessage(), e);
    }
}



}
