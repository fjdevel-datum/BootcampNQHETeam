package com.easycheck.infrastructure.adapters;

import com.easycheck.domain.ports.FacturaStoragePort;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;


@ApplicationScoped
public class GoogleDriveAdapter implements FacturaStoragePort {
    //Id de la carpeta de google
    private static final String ID_CARPETA_FACTURAS = "18tDq1dIUJ1o8U1PETGlLFFBcH5WeWVjK";
    private static final List<String> SCOPES = Collections.singletonList(
        com.google.api.services.drive.DriveScopes.DRIVE_FILE
    );

    @Inject
    @ConfigProperty(name = "google.drive.credentials.path")
    String credentialsPath;

    private final Drive driveService;
    private final NetHttpTransport HTTP_TRANSPORT;

    public GoogleDriveAdapter() throws GeneralSecurityException, IOException{
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.driveService = getDriveService(HTTP_TRANSPORT);
    }

    private Drive getDriveService(NetHttpTransport httpTransport)throws IOException
    {
        try(InputStream credentialsStream = new FileInputStream(credentialsPath)) {
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream)
                .createScoped(SCOPES);

            return new Drive.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("EasyCheck-Factura-Processor")
                .build();


        } catch (IOException e) {
            throw new IOException("Error al cargar las credenciales de Google Drive: "+ e.getMessage(), e);
        }
    }

    @Override
    public URL guardarFactura(InputStream contenido, String nombreArchivo, String mimeType)
    {

        try {

        // se crea el objeto de metadata
        File fileMetadata = new File();
        fileMetadata.setName(nombreArchivo);
        fileMetadata.setParents(List.of(ID_CARPETA_FACTURAS));

        InputStreamContent mediaContent = new InputStreamContent(mimeType, contenido);

        File uploadedFile = driveService.files()
            .create(fileMetadata, mediaContent)
            .setFields("webViewLink, id")
            .execute();

        return new URL(uploadedFile.getWebViewLink());


            
        } catch (IOException e) {
            // Esto manejaría errores de red, permisos o fallos en la subida a Drive
            throw new RuntimeException("Error al subir el archivo a Google Drive: " + e.getMessage(), e);
        }


    }


}
