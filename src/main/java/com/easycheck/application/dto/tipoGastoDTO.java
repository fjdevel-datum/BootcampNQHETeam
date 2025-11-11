package com.easycheck.application.dto;

public class tipoGastoDTO {

    private Long tipoGastoId;
    private String nombre;
    private String descripcion;

    public tipoGastoDTO() {
    }

    public tipoGastoDTO(Long tipoGastoId, String nombre, String descripcion) {
        this.tipoGastoId = tipoGastoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public Long getTipoGastoId() {
        return tipoGastoId;
    }
    public void setTipoGastoId(Long tipoGastoId) {
        this.tipoGastoId = tipoGastoId;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    



    
}
