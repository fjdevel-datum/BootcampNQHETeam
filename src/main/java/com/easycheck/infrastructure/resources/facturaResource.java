package com.easycheck.infrastructure.resources;

import com.easycheck.application.dto.FacturaUploadResponseDTO;
import com.easycheck.infrastructure.openkm.OpenKMService;
import com.easycheck.domain.model.factura;
import com.easycheck.infrastructure.repository.FacturaRepository;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

@Path("/factura")
@ApplicationScoped
public class facturaResource {

    @Inject
    OpenKMService openKMService;

    @Inject
    FacturaRepository facturaRepository;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response uploadFactura(MultipartFormDataInput input) {

        try {
            // 1. Obtener el archivo del formulario
            // El frontend debe enviar el archivo con el 'name' = 'file'
            InputPart filePart = input.getFormDataMap().get("file").get(0);
            InputStream fileStream = filePart.getBody(InputStream.class, null);
            
            // 2. Obtener el nombre original del archivo
            String originalFileName = "factura.png"; // Default
            MultivaluedMap<String, String> headers = filePart.getHeaders();
            String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");
            for (String filename : contentDisposition) {
                if (filename.trim().startsWith("filename")) {
                    originalFileName = filename.split("=")[1].trim().replaceAll("\"", "");
                    break;
                }
            }
            
            // 3. Subir a OpenKM (Paso 1 y 2 de tu propuesta)
            String comprobanteUrl = openKMService.uploadInvoice(fileStream, originalFileName);

            // 4. Guardar en la tabla factura (Paso 3 de tu propuesta)
            factura newFactura = new factura();
            newFactura.setComprobante(comprobanteUrl); // Asumo que tu entidad tiene este método
            facturaRepository.persist(newFactura);

            // 5. Devolver el ID y la URL
            FacturaUploadResponseDTO responseDto = new FacturaUploadResponseDTO(
                newFactura.getFacturaId(), 
                newFactura.getComprobante()
            );

            return Response.ok(responseDto).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
        }
    }
}