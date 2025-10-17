package com.easycheck.infrastructure.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.tarjetaDTO;
import com.easycheck.domain.service.IServiceTarjeta;
import com.easycheck.infrastructure.repository.TarjetaRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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

    @GET
    @Path("/{id}")
    @Transactional
    public Response getTarjetaById(@PathParam("id") Long id) {
        var tarjeta = TarjetaRepository.findById(id);

        if (tarjeta == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Tarjeta no encontrada con ID: " + id)
                           .build();
        }

        tarjetaDTO dto = new tarjetaDTO(
            tarjeta.getTarjetaId(),
            tarjeta.getTipoTarjeta() != null ? tarjeta.getTipoTarjeta().getTipoId() : null,
            tarjeta.getNumeroTarjeta(),
            tarjeta.getFechaExpiracion() != null ? tarjeta.getFechaExpiracion().toString() : null,
            tarjeta.getDescripcion()
        );

        return Response.ok(dto).build();
    }

    @GET
    @Path("/usuario/{usuarioId}")
    @Transactional
    public Response getTarjetasByUsuario(@PathParam("usuarioId") Long usuarioId) {
        List<tarjetaDTO> dtos = TarjetaRepository.findByUsuarioId(usuarioId).stream()
                .map(t -> new tarjetaDTO(
                        t.getTarjetaId(),
                        t.getTipoTarjeta() != null ? t.getTipoTarjeta().getTipoId() : null,
                        t.getNumeroTarjeta(),
                        t.getFechaExpiracion() != null ? t.getFechaExpiracion().toString() : null,
                        t.getDescripcion()
                ))
                .collect(Collectors.toList());

        if (dtos.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontraron tarjetas para el usuario con ID: " + usuarioId)
                    .build();
        }

        return Response.ok(dtos).build();
    }
}
