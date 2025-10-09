package com.easycheck.dto;

public class centroCostoDTO {
    private Long centroId;
    private String nombreCentro;
    private String descripcion;
    private String responsable;

    public Long getCentroId() {
        return centroId;
    }
    public void setCentroId(Long centroId) {
        this.centroId = centroId;
    }
    public String getNombreCentro() {
        return nombreCentro;
    }
    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getResponsable() {
        return responsable;
    }
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
    public centroCostoDTO(){

    }
    public centroCostoDTO(Long centroId, String nombreCentro, String descripcion, String responsable )
    {
        this.centroId = centroId;
        this.nombreCentro = nombreCentro;
        this.descripcion = descripcion;
        this.responsable = responsable;
    }
    
}
