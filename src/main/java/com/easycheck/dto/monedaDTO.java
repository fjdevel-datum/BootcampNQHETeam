package com.easycheck.dto;

public class monedaDTO {
    
    private Long monedaId;
    private String codigoISO;
    private String nombreMoneda;
    private String simbolo;

    public monedaDTO(){}

    public monedaDTO(Long monedaId, String codigoISO, String nombreMoneda, String simbolo)
    {
        this.monedaId = monedaId;
        this.codigoISO = codigoISO;
        this.nombreMoneda = nombreMoneda;
        this.simbolo = simbolo;
    }

    public Long getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(Long monedaId) {
        this.monedaId = monedaId;
    }

    public String getCodigoISO() {
        return codigoISO;
    }

    public void setCodigoISO(String codigoISO) {
        this.codigoISO = codigoISO;
    }

    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }
    
}
