package com.easycheck.infrastructure.resources;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import java.io.InputStream;

/**
 * DTO utilizado por RESTEasy Classic para mapear el formulario multipart.
 * Esta clase debe ser pública y estar en su propio archivo para cumplir con las reglas de Java.
 */
public class FacturaUploadForm {
    
    // El campo debe ser público para que RESTEasy Classic pueda acceder a él.
    @FormParam("facturaFile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream facturaFile;

    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
}
