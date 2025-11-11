package com.easycheck.infrastructure.resources;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.application.dto.RegistroUsuarioDTO;
import com.easycheck.domain.model.rol;
import com.easycheck.domain.service.IServiceAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FirebaseAuthResource {

    @Inject
    IServiceAuth serviceAuth;

    /**
     * Endpoint público para registrar un nuevo usuario
     */
    @POST
    @Path("/registro")
    @Transactional
    public Response registrarUsuario(RegistroUsuarioDTO dto) {
        try {
            EmpleadoDTO empleadoCreado = serviceAuth.registrarUsuario(dto);
            return Response.status(Response.Status.CREATED).entity(empleadoCreado).build();
        } catch (FirebaseAuthException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Error de Firebase", e.getMessage()))
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Validación fallida", e.getMessage()))
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", e.getMessage()))
                .build();
        }
    }

    /**
     * Endpoint para verificar token y obtener información completa del usuario con rol
     * Este endpoint se llama después del login en el frontend
     */
    @POST
    @Path("/verify")
    public Response verificarTokenYObtenerUsuario(@HeaderParam("Authorization") String authHeader) {
        try {
            // Validar header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Token requerido", "Debe proporcionar un token de autorización"))
                    .build();
            }

            // Extraer token
            String token = authHeader.substring(7);
            
            System.out.println("🔐 Verificando token...");

            // Verificar token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            
            System.out.println("✅ Token válido para UID: " + uid);

            // Obtener información del empleado desde la BD
            EmpleadoDTO empleado = serviceAuth.obtenerUsuarioPorUid(uid);
            
            // Crear respuesta completa con token y datos del usuario
            LoginResponse response = new LoginResponse(
                token,
                uid,
                email,
                empleado.getNombres(),
                empleado.getApellidos(),
                empleado.getRol(),
                empleado.getEmpleadoId(),
                empleado.getEmpresaId(),
                empleado.getCentroId(),
                "Autenticación exitosa"
            );

            return Response.ok(response).build();
            
        } catch (FirebaseAuthException e) {
            System.err.println("❌ Token inválido: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Token inválido", "El token proporcionado no es válido o ha expirado"))
                .build();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Usuario no encontrado", e.getMessage()))
                .build();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error interno", "Ocurrió un error al procesar la solicitud"))
                .build();
        }
    }

    /**
     * Endpoint para obtener el perfil del usuario actual
     */
    @GET
    @Path("/me")
    public Response obtenerPerfilActual(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Token requerido", "Debe estar autenticado"))
                    .build();
            }

            String token = authHeader.substring(7);
            
            // Verificar token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            
            // Obtener información del empleado
            EmpleadoDTO empleado = serviceAuth.obtenerUsuarioPorUid(uid);

            return Response.ok(empleado).build();
            
        } catch (FirebaseAuthException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Token inválido", e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Error", e.getMessage()))
                .build();
        }
    }

    // DTOs internos
    public record ErrorResponse(String error, String message) {}
    
    public record LoginResponse(
        String token,
        String uid,
        String email,
        String nombres,
        String apellidos,
        rol rol,
        Long empleadoId,
        Long empresaId,
        Long centroId,
        String mensaje
    ) {}
}