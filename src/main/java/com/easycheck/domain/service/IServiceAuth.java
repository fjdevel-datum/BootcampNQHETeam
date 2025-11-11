package com.easycheck.domain.service;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.application.dto.RegistroUsuarioDTO;
import com.google.firebase.auth.FirebaseAuthException;

public interface IServiceAuth {
    
    /**
     * Registra un nuevo usuario en Firebase y en la base de datos
     * @param dto Datos del usuario a registrar
     * @return EmpleadoDTO con los datos del empleado creado
     * @throws FirebaseAuthException Si hay error al crear el usuario en Firebase
     * @throws IllegalArgumentException Si hay error de validación
     */
    EmpleadoDTO registrarUsuario(RegistroUsuarioDTO dto) throws FirebaseAuthException, IllegalArgumentException;
    EmpleadoDTO obtenerUsuarioPorUid(String uid) throws IllegalArgumentException;
}