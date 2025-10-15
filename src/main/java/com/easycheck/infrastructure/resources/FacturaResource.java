package com.easycheck.infrastructure.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces; // Import necesario para definir la respuesta JSON
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import jakarta.ws.rs.FormParam; // Import de Jakarta JAX-RS FormParam

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.easycheck.domain.service.GoogleDriveService;

// --- DTO que representa el formulario multipart ---
// HECHO PÚBLICO para evitar el IllegalAccessException con RESTEasy Classic.


@Path("/facturas")
public class FacturaResource {

    private static final Logger LOGGER = Logger.getLogger(FacturaResource.class.getName());

    @Inject
    GoogleDriveService driveService;

    // Record para la respuesta JSON estructurada
    public record UploadResponse(String fileName, String driveUrl) {}

    @POST
    @Path("/upload-image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON) // Se usa JSON para devolver la URL y el nombre
    public Response uploadFacturaImage(@MultipartForm FacturaUploadForm form) {
        
        // 1. Verificación básica del archivo
        if (form.facturaFile == null) {
            LOGGER.log(Level.WARNING, "Intento de subida sin archivo.");
            return Response.status(Status.BAD_REQUEST)
                           .entity("El campo 'facturaFile' no puede ser nulo.").build();
        }

        try {
            // 2. Generación de nombre de archivo seguro y único
            String originalFileName = form.fileName != null && !form.fileName.trim().isEmpty() 
                                    ? form.fileName : "factura_sin_nombre";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            // Formato: factura_[timestamp]_[nombre_original]
            String safeFileName = String.format("factura_%s_%s", timestamp, originalFileName);

            // 3. Sube el archivo a Google Drive
            // Mantenemos 'application/octet-stream' como un tipo MIME genérico seguro.
            String mimeType = "application/octet-stream"; 
            String driveUrl = driveService.uploadFile(form.facturaFile, safeFileName, mimeType);

            LOGGER.log(Level.INFO, "Factura subida exitosamente. URL: {0}", driveUrl);
            
            // 4. Retorna la respuesta
            UploadResponse response = new UploadResponse(safeFileName, driveUrl);
            return Response.ok(response).build();

        } catch (Exception e) {
            // 5. Manejo de error
            LOGGER.log(Level.SEVERE, "Error crítico al subir la factura a Google Drive: " + e.getMessage(), e);
            return Response.serverError().entity("Error al subir la factura: " + e.getMessage()).build();
        }
    }
}
