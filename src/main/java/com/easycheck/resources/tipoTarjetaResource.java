package com.easycheck.resources;

import java.util.stream.Collectors;
import com.easycheck.repository.tipoTarjetaRepository;
import com.easycheck.dto.tipoTarjetaDTO;
import com.easycheck.model.tipoTarjeta;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;




@Path("/tipoTarjeta")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class tipoTarjetaResource {

    @Inject
    tipoTarjetaRepository tipoTarjetaRepository;

    @GET
    @Path("/lista")
    @Transactional
    public Response getTiposTarjeta() {
        List<tipoTarjetaDTO> dtos = tipoTarjetaRepository.listAll().stream()
            .map(t -> new tipoTarjetaDTO(t.getTipoId(), t.getTipo(), t.getDescripcion()))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }      

    @POST
    @Path("/crear")
    @Transactional
    public Response crearTipoTarjeta(tipoTarjeta tipoTarjeta) {
        tipoTarjetaRepository.persist(tipoTarjeta);
        return Response.status(Response.Status.CREATED).entity(tipoTarjeta).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarTipoTarjeta(@PathParam("id") Long id) {
        tipoTarjeta tipoTarjeta = tipoTarjetaRepository.findById(id);
        if (tipoTarjeta == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tipoTarjetaRepository.delete(tipoTarjeta);
        return Response.noContent().build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarTarjeta(@PathParam("id") Long id, tipoTarjeta tipoTarjetaActualizado) {
        tipoTarjeta tipoTarjeta = tipoTarjetaRepository.findById(id);
        if (tipoTarjeta == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tipoTarjeta.setTipo(tipoTarjetaActualizado.getTipo());
        tipoTarjeta.setDescripcion(tipoTarjetaActualizado.getDescripcion());
        tipoTarjetaRepository.persist(tipoTarjeta);
        return Response.ok(tipoTarjeta).build();
    }



}
