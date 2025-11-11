package com.easycheck.infrastructure.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.moneda;
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
     * Endpoint para obtener el perfil del usuario autenticado
     * GET /empleado/perfil
     * NOTA: Este debe ir ANTES de /{empleadoId} para evitar conflictos
     */
    @GET
    @Path("/perfil")
    public Response getPerfil() {
        try {
            System.out.println("👤 GET /empleado/perfil");
            
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
            System.out.println("Perfil obtenido: " + dto.getNombres());
            
            return Response.ok(dto).build();
        } catch (Exception e) {
            System.err.println("Error al obtener perfil: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Endpoint para listar todos los empleados
     * GET /empleado/lista
     */
    @GET
    @Path("/lista")
    public Response getEmpleado() {
        try {
            System.out.println("GET /empleado/lista");
            
            List<EmpleadoDTO> dtos = empleadoRepository.listAll().stream()
                .map(EmpleadoDTO::fromEntity)
                .collect(Collectors.toList());
            
            System.out.println("Se encontraron " + dtos.size() + " empleados");
            
            return Response.ok(dtos).build();
        } catch (Exception e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Endpoint para listar empleados por empresa
     * GET /empleado/empresa/{empresaId}
     */
    @GET
    @Path("/empresa/{empresaId}")
    public Response listarEmpleadosPorEmpresa(@PathParam("empresaId") Long empresaId) {
        try {
            System.out.println("GET /empleado/empresa/" + empresaId);
            
            List<EmpleadoDTO> empleados = serviceEmpleado.listarEmpleadosPorEmpresa(empresaId);
            
            System.out.println("Se encontraron " + empleados.size() + " empleados");
            
            return Response.ok(empleados).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", "No se pudieron obtener los empleados"))
                .build();
        }
    }

    /**
     * Obtener empleado por ID
     * GET /empleado/{empleadoId}
     * NOTA: Este debe ir AL FINAL para evitar conflictos con rutas específicas
     */
    @GET
    @Path("/{empleadoId}")
    public Response obtenerEmpleadoPorId(@PathParam("empleadoId") Long empleadoId) {
        try {
            System.out.println(" GET /empleado/" + empleadoId);
            
            EmpleadoDTO empleado = serviceEmpleado.obtenerEmpleadoPorId(empleadoId);
            
            System.out.println("Empleado encontrado: " + empleado.getNombres());
            
            return Response.ok(empleado).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("No encontrado", e.getMessage()))
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
     * Endpoint para crear empleado
     * POST /empleado/crear
     */
    @POST
    @Path("/crear")
    @Transactional
    public Response crearEmpleado(EmpleadoDTO dto) {
        try {
            System.out.println("POST /empleado/crear - Nombre: " + dto.getNombres());
            
            EmpleadoDTO respuesta = serviceEmpleado.crearEmpleado(dto);
            
            System.out.println("Empleado creado con ID: " + respuesta.getEmpleadoId());
            
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
    @Path("/{empleadoId}/moneda-base")
    public Response obtenerMonedaBase(@PathParam("empleadoId") Long empleadoId) {
        moneda monedaBase = serviceEmpleado.obtenerMonedaBasePorEmpleado(empleadoId);
        if (monedaBase == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"No se encontró la moneda base para el empleado con ID " + empleadoId + "\"}")
                    .build();
        }
        return Response.ok(monedaBase).build();
    }

    // Record para respuestas de error
    public record ErrorResponse(String error, String message) {}
}