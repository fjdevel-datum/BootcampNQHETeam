package com.easycheck.infrastructure.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.service.IServiceEmpleado;
import com.easycheck.infrastructure.repository.EmpleadoRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.container.ContainerRequestContext;

@Path("/empleado")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmpleadoResource {
    
    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    IServiceEmpleado serviceEmpleado;

    @Context
    ContainerRequestContext requestContext;

    /**
     * Endpoint para crear empleado - Solo ADMIN
     */
    @POST
    @Path("/crear")
    @Transactional
    public Response crearEmpleado(EmpleadoDTO dto) {
        try {
            EmpleadoDTO respuesta = serviceEmpleado.crearEmpleado(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Endpoint para listar todos los empleados - Solo ADMIN
     */
    @GET
    @Path("/lista")
    public Response getEmpleado() {
        try {
            List<EmpleadoDTO> dtos = empleadoRepository.listAll().stream()
                .map(EmpleadoDTO::fromEntity)
                .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Endpoint para listar empleados por empresa - Solo ADMIN
     * GET /empleado/empresa/{empresaId}
     */
    @GET
    @Path("/empresa/{empresaId}")
    public Response listarEmpleadosPorEmpresa(@PathParam("empresaId") Long empresaId) {
        try {
            System.out.println("🏢 Obteniendo empleados de la empresa ID: " + empresaId);
            
            List<EmpleadoDTO> empleados = serviceEmpleado.listarEmpleadosPorEmpresa(empresaId);
            
            System.out.println("✅ Se encontraron " + empleados.size() + " empleados");
            
            return Response.ok(empleados).build();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener empleados: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", "No se pudieron obtener los empleados"))
                .build();
        }
    }

    /**
     * Endpoint para obtener el perfil del usuario autenticado
     */
    @GET
    @Path("/perfil")
    public Response getPerfil() {
        try {
            // Obtener el empleado del contexto
            empleado empleado = (empleado) requestContext.getProperty("empleado");
            
            if (empleado == null) {
                String uid = (String) requestContext.getProperty("uid");
                empleado = serviceEmpleado.buscarPorUid(uid);
            }
            
            if (empleado == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("No encontrado", "Empleado no encontrado"))
                    .build();
            }

            EmpleadoDTO dto = EmpleadoDTO.fromEntity(empleado);
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    // Record para respuestas de error
    public record ErrorResponse(String error, String message) {}
}