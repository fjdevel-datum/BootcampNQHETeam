package com.easycheck.infrastructure.ocr;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import jakarta.ws.rs.FormParam;
import java.io.InputStream;

public class FileForm {
    
    @FormParam("file")
    @PartType("application/octet-stream")
    public InputStream file;
    
    // Constructor por defecto
    public FileForm() {}
    
    // Getters y setters (opcional, pero recomendado)
    public InputStream getFile() {
        return file;
    }
    
    public void setFile(InputStream file) {
        this.file = file;
    }
}