package com.easycheck.infrastructure.resources;

import java.util.List;

import com.easycheck.application.dto.RecursoAsignadoDTO;
import com.easycheck.application.dto.RecursoConFechasDTO;
import com.easycheck.domain.service.IServiceRecursoAsignado;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/recursoAsignado")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RecursoAsignadoResource {

    @Inject
    IServiceRecursoAsignado serviceRecurso;

    /**
     * Crear recurso asignado (asignar tarjeta a empleado)
     * POST /recursoAsignado/crear
     */
    @POST
    @Path("/crear")
    @Transactional
    public Response crearRecursoAsignado(RecursoAsignadoDTO dto) {
        try {
            System.out.println("POST /recursoAsignado/crear");
            
            RecursoAsignadoDTO respuesta = serviceRecurso.crearRecursoAsignado(dto);
            
            System.out.println("Recurso asignado con ID: " + respuesta.getRecursoId());
            
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

    /**
     * Listar todos los recursos asignados
     * GET /recursoAsignado/lista
     */
    @GET
    @Path("/lista")
    public Response getRecursos() {
        try {
            System.out.println("GET /recursoAsignado/lista");
            
            List<RecursoAsignadoDTO> dtos = serviceRecurso.listarTodosLosRecursos();
            
            System.out.println("Recursos encontrados: " + dtos.size());
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            System.err.println("Error al listar recursos: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Obtener recursos de un empleado
     * GET /recursoAsignado/empleado/{empleadoId}
     */
    @GET
    @Path("/empleado/{empleadoId}")
    public Response getRecursosByEmpleado(@PathParam("empleadoId") Long empleadoId) {
        try {
            System.out.println("GET /recursoAsignado/empleado/" + empleadoId);
            
            List<RecursoAsignadoDTO> dtos = serviceRecurso.obtenerRecursosPorEmpleado(empleadoId);
            
            System.out.println("Recursos encontrados: " + dtos.size());
            
            return Response.ok(dtos).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build(); 
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Obtener recurso por ID
     * GET /recursoAsignado/{id}
     */
    @GET
    @Path("/{id}")
    public Response getRecursoById(@PathParam("id") Long id) {
        try {
            System.out.println("GET /recursoAsignado/" + id);
            
            RecursoAsignadoDTO dto = serviceRecurso.obtenerRecursoPorId(id);
            
            System.out.println("Recurso encontrado");
            
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("No encontrado", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Obtener recurso asignado por tarjeta y empleado
     * GET /recursoAsignado/tarjeta/{tarjetaId}/empleado/{empleadoId}
     */
    @GET
    @Path("/tarjeta/{tarjetaId}/empleado/{empleadoId}")
    public Response getRecursoByTarjetaYEmpleado(
            @PathParam("tarjetaId") Long tarjetaId,
            @PathParam("empleadoId") Long empleadoId) {
        try {
            System.out.println("GET /recursoAsignado/tarjeta/" + tarjetaId + "/empleado/" + empleadoId);
            
            RecursoAsignadoDTO dto = serviceRecurso.obtenerRecursoPorTarjetaYEmpleado(tarjetaId, empleadoId);
            
            System.out.println("Recurso encontrado");
            
            return Response.ok(dto).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("No encontrado", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Actualizar recurso asignado (monto máximo y estado)
     * PUT /recursoAsignado/{id}
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizarRecursoAsignado(@PathParam("id") Long id, ActualizarRecursoDTO dto) {
        try {
            System.out.println("PUT /recursoAsignado/" + id);
            
            RecursoAsignadoDTO respuesta = serviceRecurso.actualizarRecursoAsignado(
                id, 
                dto.montoMaximo(), 
                dto.estado()
            );
            
            System.out.println("Recurso actualizado con ID: " + respuesta.getRecursoId());
            
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

    /**
     * Desactivar recurso
     * PUT /recursoAsignado/{id}/desactivar
     */
    @PUT
    @Path("/{id}/desactivar")
    @Transactional
    public Response desactivarRecurso(@PathParam("id") Long id) {
        try {
            System.out.println("PUT /recursoAsignado/" + id + "/desactivar");
            
            serviceRecurso.desactivarRecurso(id);
            
            return Response.ok(new SuccessResponse("Recurso desactivado exitosamente")).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error interno: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/empleado/{empleadoId}/para-reporte")
    public Response getRecursosParaReporte(@PathParam("empleadoId") Long empleadoId) {
        try {
            List<RecursoConFechasDTO> recursos = serviceRecurso.getRecursosConFechasParaReporte(empleadoId);
            
            return Response.ok(recursos).build();
                    
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    

    // Records para DTOs
    public record ActualizarRecursoDTO(Double montoMaximo, String estado) {}
    public record ErrorResponse(String error, String message) {}
    public record SuccessResponse(String message) {}
}