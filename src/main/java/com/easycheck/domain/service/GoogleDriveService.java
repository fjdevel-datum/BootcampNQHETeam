package com.easycheck.domain.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Startup
public class GoogleDriveService {

    @Inject
    GoogleOAuthService oAuthService;

    @ConfigProperty(name = "google.drive.parent.folder.id")
    String parentFolderId;

    @ConfigProperty(name="google.drive.credentials.path")
    String credentialsPath;

    private Drive driveService;
    private static final String APPLICATION_NAME = "Quarkus Factura Manager";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @PostConstruct
    void init()
    {
        try(InputStream in = new FileInputStream(credentialsPath)) {
            Credential credential = oAuthService.getCredential("ernesto");



            driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
            System.out.println("Servicio de google iniciado correctamente");

                    
        } catch (Exception e) {
            throw new RuntimeException("Error al iniciar Google Service: "+ e.getMessage(), e);
        }
    }

    public String uploadFile(InputStream fileData, String fileName, String mimeType)
    {
        if(driveService==null)
        {
            throw new IllegalArgumentException("Google Drive no esta inicilizando");
        }

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("upload-", fileName);
            Files.copy(fileData, tempFile, StandardCopyOption.REPLACE_EXISTING);


            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(Collections.singletonList(parentFolderId));


            java.io.File uploadFile = tempFile.toFile();
            FileContent mediaContent = new FileContent(mimeType, uploadFile);

            File file = driveService.files().create(fileMetadata,mediaContent)
            .setFields("id, webContentLink")
            .execute();
            return file.getWebContentLink();


        } catch (Exception e) {
            System.err.println("Error al subir el archivo a Google Drive: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo en la subida a Drive", e);
        } finally{
            if(tempFile!=null)
            {
                try {
                    Files.delete(tempFile);
                } catch (Exception ignore) {
                    
                }
            }
        }
    }




    
}
