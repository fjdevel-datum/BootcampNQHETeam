// ...existing code...
package com.easycheck.infrastructure.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.easycheck.domain.service.IServiceRecursoAsignado;
import com.easycheck.application.dto.InformacionRecursoDTO;

@Path("/InformacionRecurso")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InformacionRecurso {

   @Inject
     IServiceRecursoAsignado serviceRecurso;

    @GET
    @Path("/empleado/{empleadoId}")
    public InformacionRecursoDTO obtenerInformacion(@PathParam("empleadoId") Long empleadoId) {
        return serviceRecurso.obtenerInformacionPorEmpleado(empleadoId);
    }

}
