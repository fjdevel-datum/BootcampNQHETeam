package com.easycheck.infrastructure.security;

import com.easycheck.domain.model.empleado;
import com.easycheck.domain.service.IServiceEmpleado;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RolesAllowedFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    IServiceEmpleado serviceEmpleado;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        
        if (method == null) {
            return;
        }

        // Verificar si el método tiene la anotación @RolesAllowed
        RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
        
        if (rolesAllowed == null) {
            // Verificar en la clase
            rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        }

        if (rolesAllowed == null) {
            return; // No hay restricción de roles
        }

        // Obtener el UID del usuario autenticado
        String uid = (String) requestContext.getProperty("uid");
        
        if (uid == null) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Usuario no autenticado\"}")
                    .build()
            );
            return;
        }

        // Buscar el empleado por UID
        empleado empleado = serviceEmpleado.buscarPorUid(uid);
        
        if (empleado == null) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"Usuario no registrado en el sistema\"}")
                    .build()
            );
            return;
        }

        // Verificar si el rol del empleado está permitido
        String[] rolesPermitidos = rolesAllowed.value();
        String rolEmpleado = empleado.getRol().name();
        
        boolean tieneAcceso = false;
        for (String rolPermitido : rolesPermitidos) {
            if (rolPermitido.equals(rolEmpleado)) {
                tieneAcceso = true;
                break;
            }
        }

        if (!tieneAcceso) {
            requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"No tienes permisos para acceder a este recurso\"}")
                    .build()
            );
        }

        // Guardar el empleado en el contexto para uso posterior
        requestContext.setProperty("empleado", empleado);
    }
}