package com.easycheck.infrastructure.resources;

import com.easycheck.domain.service.GoogleOAuthService;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@Path("/oauth2callback")
public class OAuthCallbackResource {

    @Inject
    GoogleOAuthService oAuthService;

    @ConfigProperty(name ="google.oauth.redirect.uri")
    String redirectUri;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response oauthCallback(@QueryParam("code") String code) throws IOException {
        if (code == null || code.isBlank()) {
            throw new RuntimeException("Missing authorization code (code=null)");
        }

        GoogleAuthorizationCodeFlow flow = oAuthService.getFlow();
        TokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        flow.createAndStoreCredential(tokenResponse, "ernesto");

        return Response.ok("Autenticación exitosa. Tokens almacenados para Ernesto.").build();
    }
}
