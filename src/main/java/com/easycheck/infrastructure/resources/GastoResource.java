package com.easycheck.infrastructure.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.easycheck.application.dto.DetalleGastoDTO;
import com.easycheck.domain.service.ExcelGenerator;
import com.easycheck.domain.service.IServiceGasto;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;



@Path("/gastos")
public class GastoResource {

    @Inject
    IServiceGasto gastoService;

    @Inject
    ExcelGenerator excelGenerator;


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



    /////////////////////////////////////////////
    /// 
    @GET
    @Path("/reporte/excel")
    // Producimos un tipo de dato que el navegador entiende como un archivo Excel
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response generarReporteExcel(
            @QueryParam("empleadoId") Long empleadoId,
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFinal") String fechaFinalStr) {

        // 1. Validar y parsear entradas
        if (empleadoId == null || fechaInicioStr == null || fechaFinalStr == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los parámetros 'empleadoId', 'fechaInicio' y 'fechaFinal' son obligatorios.")
                    .build();
        }

        LocalDate fechaInicio;
        LocalDate fechaFinal;
        try {
            // Asumimos formato AAAA-MM-DD
            fechaInicio = LocalDate.parse(fechaInicioStr);
            fechaFinal = LocalDate.parse(fechaFinalStr);
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Formato de fecha inválido. Use AAAA-MM-DD.")
                    .build();
        }

        try {
            // 2. Llamar al servicio para obtener los datos
            List<DetalleGastoDTO> gastos = gastoService.getDetalleGastos(empleadoId, fechaInicio, fechaFinal);

            // 3. Generar el Excel en memoria
            ByteArrayInputStream excelStream = excelGenerator.gastosToExcel(gastos);

            // 4. Crear el nombre del archivo y devolver la respuesta
            String filename = String.format("ReporteGastos_E%d_%s.xlsx", empleadoId, fechaFinalStr);
            
            return Response.ok(excelStream)
                    // Este header le dice al navegador que descargue el archivo
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();

        } catch (IOException e) {
            // Manejar error de generación de Excel
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al generar el archivo Excel: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            // Manejar error de base de datos (de GastoServiceImpl)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al consultar los datos: " + e.getMessage())
                    .build();
        }
    }


}

