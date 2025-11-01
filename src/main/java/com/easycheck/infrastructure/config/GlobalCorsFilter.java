package com.easycheck.infrastructure.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * Filtro CORS global para permitir peticiones desde el frontend.
 * Este filtro se aplica automáticamente a TODAS las respuestas HTTP.
 */
@Provider
public class GlobalCorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, 
                      ContainerResponseContext responseContext) throws IOException {
        
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        
        // Agregar headers CORS SOLO SI NO EXISTEN (evitar duplicados)
        if (!headers.containsKey("Access-Control-Allow-Origin")) {
            headers.add("Access-Control-Allow-Origin", "https://v657nslf-5173.use2.devtunnels.ms");
        }
        
        if (!headers.containsKey("Access-Control-Allow-Credentials")) {
            headers.add("Access-Control-Allow-Credentials", "true");
        }
        
        if (!headers.containsKey("Access-Control-Allow-Methods")) {
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        }
        
        if (!headers.containsKey("Access-Control-Allow-Headers")) {
            headers.add("Access-Control-Allow-Headers", 
                "Origin, Content-Type, Accept, Authorization, X-Requested-With");
        }
        
        if (!headers.containsKey("Access-Control-Max-Age")) {
            headers.add("Access-Control-Max-Age", "86400");
        }
    }
}