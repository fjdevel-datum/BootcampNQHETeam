package com.easycheck.infrastructure.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.service.IServiceEmpleado;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.domain.service.RolesAllowed;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
    @RolesAllowed({"ADMIN"})
    public Response crearEmpleado(EmpleadoDTO dto) {
        try {
            EmpleadoDTO respuesta = serviceEmpleado.crearEmpleado(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * Endpoint para listar todos los empleados - Solo ADMIN y SUPERVISOR
     */
    @GET
    @Path("/lista")
    @RolesAllowed({"ADMIN"})
    public Response getEmpleado() {
        List<EmpleadoDTO> dtos = empleadoRepository.listAll().stream()
            .map(EmpleadoDTO::fromEntity)
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /**
     * Endpoint para obtener el perfil del usuario autenticado
     * Accesible para todos los roles autenticados
     */
    @GET
    @Path("/perfil")
    public Response getPerfil() {
        // Obtener el empleado del contexto (fue guardado por el RolesAllowedFilter)
        empleado empleado = (empleado) requestContext.getProperty("empleado");
        
        if (empleado == null) {
            String uid = (String) requestContext.getProperty("uid");
            empleado = serviceEmpleado.buscarPorUid(uid);
        }
        
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\": \"Empleado no encontrado\"}")
                .build();
        }

        EmpleadoDTO dto = new EmpleadoDTO(
            empleado.getEmpleadoId(),
            empleado.getNombres(),
            empleado.getApellidos(),
            empleado.getDocumentoIdentidad(),
            empleado.getEmpresa() != null ? empleado.getEmpresa().getEmpresaId() : null,
            empleado.getCentroCosto() != null ? empleado.getCentroCosto().getCentroId() : null,
            empleado.getUid(),
            empleado.getRol()
        );

        return Response.ok(dto).build();
    }
}