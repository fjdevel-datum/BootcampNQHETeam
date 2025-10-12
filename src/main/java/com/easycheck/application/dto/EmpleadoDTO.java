package com.easycheck.application.dto;

public class EmpleadoDTO {
    private Long empleadoId;
    private String nombres;
    private String apellidos;
    private String documentoIdentidad;
    private Long centroId;
    private Long empresaId;

    public EmpleadoDTO(){}

    public EmpleadoDTO(Long empleadoId, String nombres, String apellidos, String documentoIdentidad, Long centroId, Long empresaId)
    {
        this.empleadoId = empleadoId;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documentoIdentidad = documentoIdentidad;
        this.centroId = centroId;
        this.empresaId = empresaId;
    }

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

    public Long getCentroId() {
        return centroId;
    }

    public void setCentroId(Long centroId) {
        this.centroId = centroId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    
}
