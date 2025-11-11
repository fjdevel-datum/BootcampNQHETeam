package com.easycheck.application.dto;

import com.easycheck.domain.model.rol;

public class EmpleadoDTO {
    private Long empleadoId;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private Long empresaId;
    private Long centroId;
    private String uid;
    private rol rol;

    // Constructor vacío (necesario para Jackson/JSON)
    public EmpleadoDTO() {
    }

    // Constructor completo
    public EmpleadoDTO(Long empleadoId, String nombres, String apellidos, String documentoIdentidad, 
                       Long empresaId, Long centroId, String uid, rol rol) {
        this.empleadoId = empleadoId;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documentoIdentidad = documentoIdentidad;
        this.empresaId = empresaId;
        this.centroId = centroId;
        this.uid = uid;
        this.rol = rol;
    }

    // Constructor para crear desde empleado entity
    public static EmpleadoDTO fromEntity(com.easycheck.domain.model.empleado empleado) {
        return new EmpleadoDTO(
            empleado.getEmpleadoId(),
            empleado.getNombres(),
            empleado.getApellidos(),
            empleado.getDocumentoIdentidad(),
            empleado.getEmpresa() != null ? empleado.getEmpresa().getEmpresaId() : null,
            empleado.getCentroCosto() != null ? empleado.getCentroCosto().getCentroId() : null,
            empleado.getUid(),
            empleado.getRol()
        );
    }

    // Getters y Setters
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public rol getRol() {
        return rol;
    }

    public void setRol(rol rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}