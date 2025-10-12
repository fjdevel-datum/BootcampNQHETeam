package com.easycheck.infrastructure.resources;


import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.IncidenciaDTO;
import com.easycheck.domain.service.IServiceIncidencia;
import com.easycheck.infrastructure.repository.IncidenciaRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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
    public Response crearIncidencia(IncidenciaDTO dto)
    {
        try
        {
            IncidenciaDTO incidencia = serviceIncidencia.crearIncidencia(dto);
            return Response.status(Response.Status.CREATED).entity(incidencia).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lista")
    public Response getIncidencias()
    {
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




}
