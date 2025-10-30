package com.easycheck.infrastructure.resources;

import com.easycheck.domain.service.IServiceGasto;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/gastos")
public class GastoResource {

    @jakarta.inject.Inject
    IServiceGasto gastoService;

    @GET
    @Path("/reporte")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response generarReporte(@QueryParam("empleadoId") Long empleadoId) {

        byte[] excelBytes = gastoService.generarReporteExcel(empleadoId);

        String fileName = String.format("reporte_resumen_gastos_%d.xlsx", empleadoId);
        return Response.ok(excelBytes)
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .build();
    }
}
