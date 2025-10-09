package com.easycheck.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.dto.RecursoAsignadoDTO;
import com.easycheck.repository.RecursoAsignadoRepository;
import com.easycheck.service.IServiceRecursoAsignado;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/recursoAsignado")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RecursoAsignadoResource {

    @Inject
    RecursoAsignadoRepository recursoRepository;

    @Inject
    IServiceRecursoAsignado serviceRecurso;

    @POST
    @Path("/crear")
    @Transactional
    public Response crearRecursoAsignado(RecursoAsignadoDTO dto)
    {
        try {
            RecursoAsignadoDTO respuesta = serviceRecurso.crearRecursoAsignado(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lista")
    public Response getRecursos()
    {
        List<RecursoAsignadoDTO> dtos = recursoRepository.listAll().stream()
            .map(t -> new RecursoAsignadoDTO(
                t.getRecursoId(),
                t.getEmpleado() != null ? t.getEmpleado().getEmpleadoId() : null,
                t.getTarjeta() != null ? t.getTarjeta().getTarjetaId() : null,
                t.getFechaAsignacion() != null ? t.getFechaAsignacion().toString() : null,
                t.getMontoMaximo(),
                t.getEstado()
            )).collect(Collectors.toList());
        return Response.ok(dtos).build();

    }

    
}
