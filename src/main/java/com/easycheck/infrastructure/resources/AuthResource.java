package com.easycheck.infrastructure.resources;

import java.io.IOException;
import java.net.URI;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.azure.core.annotation.QueryParam;
import com.easycheck.domain.service.GoogleOAuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/oauth2callback")
public class AuthResource {
    @Inject
    GoogleOAuthService oAuthService;
    @ConfigProperty(name ="google.oauth.redirect.uri")
    String redirectUri;

    @GET
    @Path("/login")
    public Response login() throws IOException {
    GoogleAuthorizationCodeFlow flow = oAuthService.getFlow();
    String url = flow.newAuthorizationUrl()
            .setRedirectUri(redirectUri)
            .set("access_type", "offline")
            .set("prompt", "consent select_account") // 🔹 fuerza a elegir cuenta
            .build();
    return Response.seeOther(URI.create(url)).build();
    }

    

}
