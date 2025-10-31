package com.easycheck.infrastructure.resources;

import org.json.JSONObject;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.GastoDraftDTO;
import com.easycheck.domain.service.IServiceGasto;
import com.easycheck.infrastructure.ocr.service.DraftGastoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("ocr/guardarGasto")
public  class OCRGastoResource {
    
    @Inject
    DraftGastoService draftGastoService;

    @Inject
    IServiceGasto serviceGasto;

    
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveGasto(GastoDraftDTO draft) {
        
        String geminiJson = draftGastoService.getAndRemoveDraft(draft.getDraftId());

        if (geminiJson == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Draft ID no válido o expirado.\"}").build();
        }

        try {
            // 1. Parsear el JSON de Gemini para extraer los datos.
            JSONObject geminiData = new JSONObject(geminiJson);
            
            // 2. Crear el DTO final que GastoService espera
            GastoDTO gastoDTO = new GastoDTO();
            
            // Mapear datos de Gemini
            gastoDTO.setFecha(geminiData.getString("Fecha"));
            gastoDTO.setTotalGasto(geminiData.getDouble("Monto_Total"));
            gastoDTO.setDescripcionGasto(geminiData.getString("Descripcion_Item"));
            
            // Mapear datos manuales del usuario
            gastoDTO.setMonedaGasto(draft.getMonedaGasto());
            gastoDTO.setFacturaId(draft.getFacturaId());
            gastoDTO.setRecursoId(draft.getRecursoId());
            gastoDTO.setTipoGastoId(draft.getTipoGastoId());
            gastoDTO.setActividadId(draft.getActividadId());
            gastoDTO.setTotalMonedaBase(draft.getTotalMonedaBase());

            // 3. Llamar a la lógica de guardado
            GastoDTO savedGasto = serviceGasto.crearGasto(gastoDTO);

            return Response.ok(savedGasto).build();
            
        } catch (IllegalArgumentException e) {
            // Errores de validación de tu GastoService
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(String.format("{\"error\": \"Validación: %s\"}", e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(String.format("{\"error\": \"Error al guardar el gasto: %s\"}", e.getMessage())).build();
        }
    }
}