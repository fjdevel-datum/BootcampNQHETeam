package com.easycheck.infrastructure.openkm;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
// ¡NUEVA IMPORTACIÓN!
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;

@RegisterRestClient(configKey = "openkm-api")
@Path("/services/rest/document")
public interface OpenKMClient {

    @POST
    @Path("/createSimple")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response createSimple(
        // ¡CAMBIO AQUÍ! Ya no usamos @FormParam
        MultipartFormDataOutput formData, 
        @HeaderParam("Authorization") String authHeader
    );
}