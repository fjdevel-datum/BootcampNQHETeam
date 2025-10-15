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

@Path("/auth")
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
                .build();
        return Response.seeOther(URI.create(url)).build();
    }

    @GET
    @Path("/callback")
    public Response oauthCallback(@QueryParam("code") String code) throws IOException
    {
        GoogleAuthorizationCodeFlow flow = oAuthService.getFlow();
        TokenResponse tokenResponse = flow.newTokenRequest(code)
        .setRedirectUri(redirectUri)
        .execute();

        Credential credential = flow.createAndStoreCredential(tokenResponse, "ernesto");
        return Response.ok("Autenticacion exitosa. Tokens almacenados para Ernesto").build();
    }

}
