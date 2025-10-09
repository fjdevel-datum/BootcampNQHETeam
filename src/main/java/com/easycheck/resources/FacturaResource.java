package com.easycheck.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.easycheck.ocr.OCRService;

import java.io.*;

@Path("/facturas")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class FacturaResource {

    @Inject
    OCRService ocrService;

    @POST
    @Path("/upload")
    public Response subirFactura(MultipartFormDataInput input) {
        try {
            var upload = input.getFormDataPart("file", InputStream.class, null);
            File tempFile = File.createTempFile("factura_", ".jpg");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                upload.transferTo(out);
            }

            String textoExtraido = ocrService.procesarImagen(tempFile);

            // Puedes luego procesar el texto para extraer monto, fecha, proveedor, etc.
            return Response.ok().entity("{\"texto\":\"" + textoExtraido + "\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error procesando la imagen.\"}")
                    .build();
        }
    }
}
