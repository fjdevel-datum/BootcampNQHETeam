package com.easycheck.infrastructure.resources;

import org.json.JSONObject;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.GastoDraftDTO;
import com.easycheck.application.dto.OCRGastoRequestDTO;
import com.easycheck.domain.service.IServiceGasto;
import com.easycheck.domain.service.ServiceOCRGasto;
import com.easycheck.infrastructure.ocr.service.DraftGastoService;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ocr/guardarGasto")
public class OCRGastoResource {
    
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
            JSONObject geminiData = new JSONObject(geminiJson);
            
            // Crear el DTO final combinando datos de Gemini y del usuario
            GastoDTO gastoDTO = new GastoDTO();
            
            // Mapear datos de Gemini
            gastoDTO.setFecha(geminiData.optString("Fecha", ""));
            gastoDTO.setTotalGasto(geminiData.optDouble("Monto_Total", 0.0));
            gastoDTO.setDescripcionGasto(geminiData.optString("Descripcion_Item", ""));
            
            // Mapear datos manuales del usuario
            gastoDTO.setMonedaGasto(draft.getMonedaGasto());
            
            // ⚠️ IMPORTANTE: Solo asignar facturaId si no es null
            if (draft.getFacturaId() != null) {
                gastoDTO.setFacturaId(draft.getFacturaId());
            }
            
            gastoDTO.setRecursoId(draft.getRecursoId());
            gastoDTO.setTipoGastoId(draft.getTipoGastoId());
            gastoDTO.setActividadId(draft.getActividadId());
            gastoDTO.setTotalMonedaBase(draft.getTotalMonedaBase());

            // Guardar el gasto
            GastoDTO savedGasto = serviceGasto.crearGasto(gastoDTO);

            return Response.ok(savedGasto).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(String.format("{\"error\": \"%s\"}", e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error completo en los logs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(String.format("{\"error\": \"Error al guardar el gasto: %s\"}", 
                            e.getMessage())).build();
        }
    }

    ////////////////////
    // Inyectamos el NUEVO servicio de un solo paso
    @Inject
    ServiceOCRGasto serviceOCRGasto;

    // El 'DraftGastoService' y el 'IServiceGasto' antiguo ya no se usan aquí

    /**
     * Este es el NUEVO endpoint de un solo paso.
     * Recibe el archivo y todos los IDs en una sola llamada multipart.
     * El frontend debe llamar a este endpoint en lugar de los dos de antes.
     */
    @POST
    @Path("/procesarGuardar")
    @Consumes(MediaType.MULTIPART_FORM_DATA) // 4. Acepta archivos y datos de formulario
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional // Es buena idea manejar la transacción aquí
    public Response procesarYGuardarGasto(@BeanParam OCRGastoRequestDTO requestDTO) {
        
        // Validamos que el archivo no venga nulo
        if (requestDTO.file == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"No se proporcionó ningún archivo.\"}")
                    .build();
        }

        try {
            // 5. Llamamos al ÚNICO método del nuevo servicio
            
            GastoDTO gastoGuardado = serviceOCRGasto.procesarFacturayGuardarGasto(requestDTO);
            
            // 6. Retornamos el resultado
            return Response.ok(gastoGuardado).build();
            
        } catch (IllegalArgumentException e) {
            // Errores de validación (ej. ID no encontrado, fecha inválida)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(String.format("{\"error\": \"%s\"}", e.getMessage()))
                    .build();
        } catch (Exception e) {
            // Errores inesperados (ej. OpenKM, OCR, Conexión, etc.)
            e.printStackTrace(); // Para ver el error completo en los logs
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(String.format("{\"error\": \"Error al procesar el gasto: %s\"}", 
                            e.getMessage()))
                    .build();
        }
    }
    
    // 
    // El método /save que tenías antes (con DraftGastoService)
    // queda obsoleto y debe ser eliminado.
    //












}