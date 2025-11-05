package com.easycheck.infrastructure.openkm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class OpenKMService {
    
    @Inject
   @RestClient
    OpenKMClient openKMClient;
    
    @ConfigProperty(name = "openkm.username")
    String username;
    @ConfigProperty(name = "openkm.password")
    String password;

    private static final String OPENKM_FOLDER_PATH = "/okm:root/facturas";

    /**
     * Sube una factura a OpenKM.
     * @param fileStream El stream del archivo a subir.
     * @return El path completo del documento guardado en OpenKM (ej. /okm:root/facturas/...).
    */

    public String uploadInvoice(InputStream fileStream) {
        // 1. Generamos un nombre de archivo único
        String fileName = UUID.randomUUID().toString() + ".png"; // Asumimos PNG o JPG
        String docPath = OPENKM_FOLDER_PATH + fileName;

        // 2. Creamos la cabecera de autenticación básica
        String authString = username + ":" + password;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());

        // 3. Llamamos al cliente REST
        try (Response response = openKMClient.createSimple(docPath, fileStream, authHeader)) {
            
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                // Si algo falla, lanzamos un error
                throw new InternalServerErrorException("Error al subir archivo a OpenKM: " + response.getStatus());
            }

            // 4. Retornamos el path que usaremos como "comprobante"
            return docPath;
        } catch (Exception e) {
            throw new InternalServerErrorException("Fallo la conexión con OpenKM: " + e.getMessage());
        }
    }




}
