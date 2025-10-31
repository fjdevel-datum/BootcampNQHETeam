package com.easycheck.infrastructure.ocr;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.easycheck.infrastructure.ocr.service.DraftGastoService;
import com.easycheck.infrastructure.ocr.service.Extractor;


@Path("/ocr2")
public class OCRResourceCopy {

    @Inject
    OCRService ocrService;

    @Inject
    Extractor extractor;

    @Inject
    DraftGastoService draftGastoService;

    @POST
    @Path("/extract2")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractTextFromImage(@MultipartForm FileForm form) {
        try {
            // Verificar que se haya subido un archivo
            if (form == null || form.file == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"No file uploaded\"}")
                        .build();
            }

         // 1. Extraer texto usando el servicio OCR
            String extractedText = ocrService.extractText(form.file);

            // 2. Usar Gemini para estructurar y formatear el JSON
            String structuredJson = extractor.generateGastoJson(extractedText);
            
            // 3. Guardar el JSON temporalmente y obtener un ID
            String draftId = draftGastoService.saveDraft(structuredJson);
            
            // 4. Devolver el JSON estructurado y el ID al front-end
            // El front-end puede usar el JSON para pre-llenar y guardar el ID.
            String responseJson = String.format("{\"draftId\": \"%s\", \"geminiData\": %s}",
                    draftId, structuredJson);
            
            return Response.ok(responseJson).build();
            
        } catch (Exception e) {
            String errorResponse = String.format("{\"error\": \"Error processing file: %s\"}", 
                    e.getMessage().replace("\"", "\\\""));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .build();
        }
    }


    //enviar la informacion del json estructurado por gemini
    






}