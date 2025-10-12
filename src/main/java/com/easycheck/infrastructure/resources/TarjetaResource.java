package com.easycheck.infrastructure.resources;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.tarjetaDTO;
import com.easycheck.domain.service.IServiceTarjeta;
import com.easycheck.infrastructure.repository.TarjetaRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;


@Path("/tarjeta")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TarjetaResource {

    @Inject
    TarjetaRepository TarjetaRepository;

    @Inject
    IServiceTarjeta serviceTarjeta;


    @POST
    @Path("/crearTarjeta")
    @Transactional
   public Response crearTarjeta(tarjetaDTO dto) {
        try {
            tarjetaDTO respuesta = serviceTarjeta.crearTarjeta(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lista")
    @Transactional
    public Response getTarjetas() {
        List<tarjetaDTO> dtos = TarjetaRepository.listAll().stream()
                .map(t -> new tarjetaDTO(
                t.getTarjetaId(),
                t.getTipoTarjeta() != null ? t.getTipoTarjeta().getTipoId() : null,
                t.getNumeroTarjeta(),
                t.getFechaExpiracion() != null ? t.getFechaExpiracion().toString() : null,
                t.getDescripcion()))
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }


}
