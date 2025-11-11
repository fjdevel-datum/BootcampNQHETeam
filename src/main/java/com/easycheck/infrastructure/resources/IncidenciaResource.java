package com.easycheck.infrastructure.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.IncidenciaDTO;
import com.easycheck.application.dto.IncidenciaDetalleDTO;
import com.easycheck.domain.service.IServiceIncidencia;
import com.easycheck.infrastructure.repository.IncidenciaRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;

@Path("/incidencia")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IncidenciaResource {
    
    @Inject
    IncidenciaRepository incidenciaRepository;

    @Inject
    IServiceIncidencia serviceIncidencia;

    @POST
    @Path("/crear")
    public Response crearIncidencia(IncidenciaDTO dto) {
        try {
            IncidenciaDTO incidencia = serviceIncidencia.crearIncidencia(dto);
            return Response.status(Response.Status.CREATED).entity(incidencia).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lista")
    public Response getIncidencias() {
        List<IncidenciaDTO> dtos = incidenciaRepository.listAll().stream()
            .map(t -> new IncidenciaDTO(
                t.getIncidenciaId(),
                t.getEmpleado() != null ? t.getEmpleado().getEmpleadoId() : null,
                t.getTipoIncidencia() != null ? t.getTipoIncidencia().getTipoIncidenciaId() : null,
                t.getRecursoAsignado() != null ? t.getRecursoAsignado().getRecursoId() : null,
                t.getFechaIncidencia().toString(),
                t.getDescripcion()
            )).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/empleado/{empleadoId}")
    public Response listarPorEmpleado(@PathParam("empleadoId") Long empleadoId) {
        try {
            System.out.println("GET /incidencia/empleado/" + empleadoId);
            
            List<IncidenciaDetalleDTO> incidencias = serviceIncidencia.listarPorEmpleado(empleadoId);
            
            System.out.println("Incidencias encontradas: " + incidencias.size());
            
            return Response.ok(incidencias).build();
            
        } catch (Exception e) {
            System.err.println("Error al listar incidencias: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error interno: " + e.getMessage())
                .build();
        }
    }

    @GET
    @Path("/empresa/{empresaId}")
    public Response listarPorEmpresa(@PathParam("empresaId") Long empresaId) {
        try {
            System.out.println("GET /incidencia/empresa/" + empresaId);
            
            if (empresaId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID de la empresa es requerido")
                    .build();
            }
            
            List<IncidenciaDetalleDTO> incidencias = serviceIncidencia.listarPorEmpresa(empresaId);
            
            System.out.println("Incidencias encontradas para la empresa: " + incidencias.size());
            
            return Response.ok(incidencias).build();
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
        } catch (Exception e) {
            System.err.println("Error al listar incidencias por empresa: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error interno: " + e.getMessage())
                .build();
        }
    }
}