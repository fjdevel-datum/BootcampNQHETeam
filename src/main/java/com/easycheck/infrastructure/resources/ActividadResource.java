package com.easycheck.infrastructure.resources;

import com.easycheck.application.dto.ActividadDTO;
import com.easycheck.application.dto.ActividadListDTO;
import com.easycheck.domain.service.IServiceActividad;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/actividad")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadResource {

    @Inject
    IServiceActividad actividadService;

    /**
     * Obtener todas las actividades de un empleado
     * Endpoint: GET /actividad/empleado/{empleadoId}
     */
    @GET
    @Path("/empleado/{empleadoId}")
    public Response obtenerActividadesPorEmpleado(@PathParam("empleadoId") Long empleadoId) {
        try {
            System.out.println("Obteniendo actividades para empleado ID: " + empleadoId);
            
            List<ActividadListDTO> actividades = actividadService.getActividadByEmpleadoId(empleadoId);
            
            System.out.println("Se encontraron " + actividades.size() + " actividades");
            
            return Response.ok(actividades).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error al obtener actividades: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", "No se pudieron obtener las actividades"))
                .build();
        }
    }

    /**
     * Crear una nueva actividad
     * Endpoint: POST /actividad
     */
    @POST
    public Response crearActividad(ActividadDTO dto) {
        try {
            System.out.println("Creando nueva actividad: " + dto.getNombre());
            
            ActividadDTO nuevaActividad = actividadService.crearActividad(dto);
            
            System.out.println("Actividad creada con ID: " + nuevaActividad.getActividadId());
            
            return Response.status(Response.Status.CREATED).entity(nuevaActividad).build();
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de validación", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("Error al crear actividad: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", "No se pudo crear la actividad"))
                .build();
        }
    }

    // Record para respuestas de error
    public record ErrorResponse(String error, String message) {}
}