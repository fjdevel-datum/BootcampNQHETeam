package com.easycheck.application.dto;

public class tarjetaDTO {
    private Long tarjetaId;
    private Long tipoId;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private String descripcion;
    private Integer diaCorte;

    public tarjetaDTO() {}

    public tarjetaDTO(Long tarjetaId, Long tipoId, String numeroTarjeta, String fechaExpiracion, String descripcion, Integer diaCorte) {
        this.tarjetaId = tarjetaId;
        this.tipoId = tipoId;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.descripcion = descripcion;
        this.diaCorte = diaCorte;
    }

    public Long getTarjetaId() {
        return tarjetaId;
    }
    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }
    public Long getTipoId() {
        return tipoId;
    }
    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }

    public Integer getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
    public String getFechaExpiracion() {
        return fechaExpiracion;
    }
    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}