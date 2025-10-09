package com.easycheck.resources;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.easycheck.dto.ActividadDTO;
import com.easycheck.repository.ActividadRepository;
import com.easycheck.service.IServiceActividad;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/actividad")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ActividadResource {

    @Inject
    ActividadRepository actividadRepository;

    @Inject
    IServiceActividad serviceActividad;

    @GET
    @Path("/lista")
    public Response getActividades() {
        List<ActividadDTO> dtos = actividadRepository.listAll().stream()
            .map(t-> new ActividadDTO(
                t.getActividadId(),
                t.getNombre(),
                t.getFechaInicio() != null ? t.getFechaInicio().toString() : null,
                t.getFechaFinal() != null ? t.getFechaFinal().toString() : null,
                t.getEstado(),
                t.getEmpleado() != null ? t.getEmpleado().getEmpleadoId() : null
            )).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crearActividad(ActividadDTO actividad) {
        try{
            ActividadDTO respDto = serviceActividad.crearActividad(actividad);
            return Response.status(Response.Status.CREATED).entity(respDto).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        
    }

 



}