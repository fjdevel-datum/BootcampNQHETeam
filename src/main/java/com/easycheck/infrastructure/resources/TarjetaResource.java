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
    TarjetaRepository tarjetaRepository;

    @Inject
    IServiceTarjeta serviceTarjeta;

    @POST
    @Path("/crearTarjeta")
    @Transactional
    public Response crearTarjeta(tarjetaDTO dto) {
        try {
            System.out.println("POST /tarjeta/crearTarjeta");
            tarjetaDTO respuesta = serviceTarjeta.crearTarjeta(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error de validación", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/lista")
    @Transactional
    public Response getTarjetas() {
        try {
            System.out.println("GET /tarjeta/lista");
            
            List<tarjetaDTO> dtos = tarjetaRepository.listAll().stream()
                    .map(t -> new tarjetaDTO(
                            t.getTarjetaId(),
                            t.getTipoTarjeta() != null ? t.getTipoTarjeta().getTipoId() : null,
                            t.getNumeroTarjeta(),
                            t.getFechaExpiracion() != null ? t.getFechaExpiracion().toString() : null,
                            t.getDescripcion()))
                    .collect(Collectors.toList());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            System.err.println("Error al listar tarjetas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Transactional
    public Response getTarjetaById(@PathParam("id") Long id) {
        try {
            System.out.println("GET /tarjeta/" + id);
            
            var tarjeta = tarjetaRepository.findById(id);

            if (tarjeta == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("No encontrado", "Tarjeta no encontrada con ID: " + id))
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
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/usuario/{usuarioId}")
    @Transactional
    public Response getTarjetasByUsuario(@PathParam("usuarioId") Long usuarioId) {
        try {
            System.out.println("GET /tarjeta/usuario/" + usuarioId);
            
            List<tarjetaDTO> dtos = tarjetaRepository.findByUsuarioId(usuarioId).stream()
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
                        .entity(new ErrorResponse("No encontrado", "No se encontraron tarjetas para el usuario con ID: " + usuarioId))
                        .build();
            }

            return Response.ok(dtos).build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    /**
     * ⭐ NUEVO ENDPOINT - Actualizar fecha de expiración de una tarjeta
     * PUT /tarjeta/{id}/fecha-expiracion
     */
    @PUT
    @Path("/{id}/fecha-expiracion")
    @Transactional
    public Response actualizarFechaExpiracion(@PathParam("id") Long id, FechaExpiracionDTO dto) {
        try {
            System.out.println("PUT /tarjeta/" + id + "/fecha-expiracion");
            System.out.println("Nueva fecha: " + dto.fechaExpiracion());
            
            tarjetaDTO respuesta = serviceTarjeta.actualizarFechaExpiracion(id, dto.fechaExpiracion());
            
            System.out.println("Fecha actualizada exitosamente");
            
            return Response.ok(respuesta).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Error de validación", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error interno", e.getMessage()))
                    .build();
        }
    }

    // Records para DTOs
    public record FechaExpiracionDTO(String fechaExpiracion) {}
    public record ErrorResponse(String error, String message) {}
}