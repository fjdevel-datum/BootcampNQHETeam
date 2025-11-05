package com.easycheck.infrastructure.openkm;

import java.io.InputStream;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RegisterRestClient(configKey = "openkm-api")
@Path("/services/rest/document")
public interface OpenKMClient {
    
    @POST
    @Path("/createSimple")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    Response createSimple(
        @FormParam("docPath") String docPath,
        @FormParam("content") InputStream content,
        @HeaderParam("Authorization") String authHeader
    );

}
