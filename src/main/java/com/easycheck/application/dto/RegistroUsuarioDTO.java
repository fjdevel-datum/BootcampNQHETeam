package com.easycheck.application.dto;

import com.easycheck.domain.model.rol;

public class RegistroUsuarioDTO {
    
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private Long empresaId;
    private Long centroId;
    private rol rol;

    // Constructor vacío (necesario para Jackson)
    public RegistroUsuarioDTO() {
    }

    // Constructor completo
    public RegistroUsuarioDTO(String email, String password, String nombres, String apellidos, 
                             String documentoIdentidad, Long empresaId, Long centroId, rol rol) {
        this.email = email;
        this.password = password;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documentoIdentidad = documentoIdentidad;
        this.empresaId = empresaId;
        this.centroId = centroId;
        this.rol = rol;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getCentroId() {
        return centroId;
    }

    public void setCentroId(Long centroId) {
        this.centroId = centroId;
    }

    public rol getRol() {
        return rol;
    }

    public void setRol(rol rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "RegistroUsuarioDTO{" +
                "email='" + email + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", empresaId=" + empresaId +
                ", centroId=" + centroId +
                ", rol=" + rol +
                '}';
    }
}