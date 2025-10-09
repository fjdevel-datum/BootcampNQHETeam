package com.easycheck.dto;

public class tipoTarjetaDTO {
    private Long tipoId;
    private String tipo;
    private String descripcion;

    public tipoTarjetaDTO() {
    }

    public tipoTarjetaDTO(Long tipoId, String tipo, String descripcion) {
        this.tipoId = tipoId;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Long getTipoId() {
        return tipoId;
    }

    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}