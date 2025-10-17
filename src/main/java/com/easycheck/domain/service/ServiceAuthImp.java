package com.easycheck.domain.service;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.application.dto.RegistroUsuarioDTO;
import com.easycheck.domain.model.centroCosto;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.empresa;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.centroCostoRepository;
import com.easycheck.infrastructure.repository.empresaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServiceAuthImp implements IServiceAuth {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    empresaRepository empresaRepository;

    @Inject
    centroCostoRepository centroCostoRepository;

    @Override
    @Transactional
    public EmpleadoDTO registrarUsuario(RegistroUsuarioDTO dto) throws FirebaseAuthException, IllegalArgumentException {
        
        // 1. Validar datos básicos
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        if (dto.getNombres() == null || dto.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres son requeridos");
        }

        if (dto.getApellidos() == null || dto.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son requeridos");
        }

        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("El ID de empresa es requerido");
        }

        if (dto.getCentroId() == null) {
            throw new IllegalArgumentException("El ID de centro de costo es requerido");
        }

        // 2. Validar empresa y centro de costo
        empresa empresa = empresaRepository.findById(dto.getEmpresaId());
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa no encontrada con ID: " + dto.getEmpresaId());
        }

        centroCosto centroCosto = centroCostoRepository.findById(dto.getCentroId());
        if (centroCosto == null) {
            throw new IllegalArgumentException("Centro de costo no encontrado con ID: " + dto.getCentroId());
        }

        // 3. Verificar si el documento de identidad ya existe (si se proporciona)
        if (dto.getDocumentoIdentidad() != null && !dto.getDocumentoIdentidad().trim().isEmpty()) {
            empleado empleadoPorDocumento = empleadoRepository
                .find("documentoIdentidad", dto.getDocumentoIdentidad())
                .firstResult();
            if (empleadoPorDocumento != null) {
                throw new IllegalArgumentException("Ya existe un empleado con este documento de identidad");
            }
        }

        // 4. Crear usuario en Firebase
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
            .setEmail(dto.getEmail())
            .setPassword(dto.getPassword())
            .setDisplayName(dto.getNombres() + " " + dto.getApellidos())
            .setEmailVerified(false);

        UserRecord userRecord;
        try {
            userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("✅ Usuario creado en Firebase con UID: " + userRecord.getUid());
        } catch (FirebaseAuthException e) {
            System.err.println("❌ Error Firebase: " + e.getMessage());
            
            // Manejar errores específicos de Firebase
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("email-already-exists") || 
                errorMessage.contains("EMAIL_EXISTS"))) {
                throw new IllegalArgumentException("El email ya está registrado en Firebase");
            }
            throw e;
        }

        // 5. Crear empleado en la base de datos
        empleado nuevoEmpleado = new empleado();
        nuevoEmpleado.setNombres(dto.getNombres());
        nuevoEmpleado.setApellidos(dto.getApellidos());
        nuevoEmpleado.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoEmpleado.setEmpresa(empresa);
        nuevoEmpleado.setCentroCosto(centroCosto);
        nuevoEmpleado.setUid(userRecord.getUid()); // UID de Firebase
        nuevoEmpleado.setRol(dto.getRol() != null ? dto.getRol() : com.easycheck.domain.model.rol.EMPLEADO);

        try {
            empleadoRepository.persist(nuevoEmpleado);
            empleadoRepository.flush(); // Forzar sincronización con BD
            System.out.println("✅ Empleado guardado en BD con ID: " + nuevoEmpleado.getEmpleadoId());
        } catch (Exception e) {
            System.err.println("Error al guardar empleado en BD: " + e.getMessage());
            e.printStackTrace();
            
            // Si falla la creación en BD, eliminar el usuario de Firebase (rollback)
            try {
                FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
                System.out.println("Usuario eliminado de Firebase (rollback exitoso)");
            } catch (FirebaseAuthException deleteException) {
                System.err.println("Error al revertir creación en Firebase: " + deleteException.getMessage());
            }
            
            throw new RuntimeException("Error al guardar el empleado en la base de datos: " + e.getMessage(), e);
        }

        // 6. Retornar el DTO
        return EmpleadoDTO.fromEntity(nuevoEmpleado);
    }
    @Override
public EmpleadoDTO obtenerUsuarioPorUid(String uid) throws IllegalArgumentException {
    
    if (uid == null || uid.trim().isEmpty()) {
        throw new IllegalArgumentException("UID requerido");
    }

    System.out.println("🔍 Buscando empleado con UID: " + uid);

    // Buscar empleado por firebase_uid
    empleado empleado = empleadoRepository.find("firebase_uid", uid).firstResult();
    
    if (empleado == null) {
        System.err.println("Empleado no encontrado para UID: " + uid);
        throw new IllegalArgumentException("Empleado no encontrado");
    }

    System.out.println("✅ Empleado encontrado: " + empleado.getNombres() + " - Rol: " + empleado.getRol());

    return EmpleadoDTO.fromEntity(empleado);
}
}
