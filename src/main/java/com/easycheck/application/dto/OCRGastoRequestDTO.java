package com.easycheck.application.dto;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;

public class OCRGastoRequestDTO {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public InputStream file;

    @FormParam("actividadId")
    public Long actividadId;

    @FormParam("tipoGastoId")
    public Long tipoGastoId;

    @FormParam("monedaId")
    public Long monedaId;

    @FormParam("recursoId")
    public Long recursoId;

    
}