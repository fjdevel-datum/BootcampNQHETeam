package com.easycheck.infrastructure.openkm;
import com.easycheck.infrastructure.openkm.OpenKMClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
// ¡NUEVAS IMPORTACIONES!
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import jakarta.ws.rs.core.MediaType;

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

    private static final String OPENKM_FOLDER_PATH = "/okm:root/facturas/";

    public String uploadInvoice(InputStream fileStream, String originalFileName) {
        
        // ... (La lógica del nombre de archivo y docPath sigue igual) ...
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        String fileName = UUID.randomUUID().toString() + extension;
        String docPath = OPENKM_FOLDER_PATH + fileName;

        // ... (La lógica de autenticación sigue igual) ...
        String authString = username + ":" + password;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());

        // --- ¡NUEVA LÓGICA AQUÍ! ---
        // 3. Construir el cuerpo multipart manualmente
        MultipartFormDataOutput formData = new MultipartFormDataOutput();
        
        // Añade la parte del 'path' como texto
        formData.addFormData("docPath", docPath, MediaType.TEXT_PLAIN_TYPE);
        
        // Añade la parte del archivo (el 'content')
        formData.addFormData("content", fileStream, MediaType.APPLICATION_OCTET_STREAM_TYPE, originalFileName);
        // --- FIN DE LA NUEVA LÓGICA ---

        // 4. Llamamos al cliente REST
        try (Response response = openKMClient.createSimple(formData, authHeader)) { // Pasamos el objeto formData
            
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new InternalServerErrorException("Error al subir archivo a OpenKM: " + response.getStatus());
            }
            return docPath;
        } catch (Exception e) {
            e.printStackTrace(); // Importante para ver el error real
            throw new InternalServerErrorException("Fallo la conexión con OpenKM: " + e.getMessage());
        }
    }
}