package com.easycheck.infrastructure.resources;

import com.easycheck.application.dto.FacturaDTO;
import com.easycheck.domain.service.IServiceFactura;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.oracle.svm.core.annotate.Inject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/facturas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FacturaResuorce {

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    IServiceFactura serviceFactura;

    @POST
    @Path("/crearFacruta")
    public Response crearFactura(FacturaDTO dto)
    {
        FacturaDTO factura = serviceFactura.crearFactura(dto);
        return Response.status(Response.Status.CREATED).entity(factura).build();
    }
    
}
