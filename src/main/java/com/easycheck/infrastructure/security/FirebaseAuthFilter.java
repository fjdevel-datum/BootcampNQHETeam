package com.easycheck.infrastructure.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class FirebaseAuthFilter implements ContainerRequestFilter {

    // Lista de rutas públicas que NO requieren autenticación
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/auth/registro",
        "/auth/login",
        "/public",
        "/health",
        "/q/health",
        "/q/dev",
        "/q/swagger-ui",
        "/q/openapi"
    );

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Permitir requests OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        // Verificar si la ruta es pública
        if (isPublicPath(path)) {
            System.out.println("✅ Acceso público permitido: " + path);
            return;
        }

        // Para rutas protegidas, validar el token
        String authorizationHeader = requestContext.getHeaderString("Authorization");
        
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("❌ Token no proporcionado para: " + path);
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Token de autorización requerido\"}")
                    .type("application/json")
                    .build()
            );
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            
            System.out.println("✅ Token válido - Usuario: " + uid + " - Ruta: " + path);
            
            // Crear el SecurityContext con la información del usuario
            requestContext.setSecurityContext(new FirebaseSecurityContext(uid, decodedToken));
            
            // Guardar información del usuario en propiedades del contexto
            requestContext.setProperty("uid", uid);
            requestContext.setProperty("firebaseUid", uid);
            requestContext.setProperty("firebaseEmail", decodedToken.getEmail());
            requestContext.setProperty("firebaseToken", decodedToken);
            
        } catch (FirebaseAuthException e) {
            System.out.println("❌ Token inválido: " + e.getMessage() + " - Ruta: " + path);
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Token inválido o expirado\"}")
                    .type("application/json")
                    .build()
            );
        }
    }

    /**
     * Verifica si una ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
            .anyMatch(publicPath -> path.startsWith(publicPath) || path.contains(publicPath));
    }

    /**
     * SecurityContext personalizado para Firebase
     */
    private static class FirebaseSecurityContext implements SecurityContext {
        private final String uid;
        private final FirebaseToken token;

        public FirebaseSecurityContext(String uid, FirebaseToken token) {
            this.uid = uid;
            this.token = token;
        }

        @Override
        public Principal getUserPrincipal() {
            return () -> uid;
        }

        @Override
        public boolean isUserInRole(String role) {
            // Verificar roles desde los custom claims de Firebase
            Object customRole = token.getClaims().get("role");
            if (customRole != null) {
                return role.equals(customRole.toString());
            }
            return false;
        }

        @Override
        public boolean isSecure() {
            return true;
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}