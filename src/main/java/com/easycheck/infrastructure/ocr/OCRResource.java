package com.easycheck.infrastructure.ocr;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import com.easycheck.infrastructure.ocr.service.Extractor;


@Path("/ocr")
public class OCRResource {

    @Inject
    OCRService ocrService;

    @Inject
    Extractor extractor;

    @POST
    @Path("/extract")
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

            // Extraer texto usando el servicio OCR
            String extractedText = ocrService.extractText(form.file);

            //Usar LLaMA para estructurar y formatear el JSON
            String structuredJson = extractor.generateGastoJson(extractedText);
            
            
            // Crear respuesta JSON
            String jsonResponse = String.format("{\"text\": \"%s\"}", 
                    extractedText.replace("\"", "\\\"").replace("\n", "\\n"));
            
            return Response.ok(structuredJson).build();
            
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