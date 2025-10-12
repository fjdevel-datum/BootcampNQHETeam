package com.easycheck.application.dto;



public class empresaDTO {
    
    private Long empresaId;
    private String empresaNombre;
    private String ubicacion;
    private Long paisId;
    private Long monedaId;

    public empresaDTO(){

    }

    public empresaDTO(Long empresaId,String empresaNombre,String ubicacion,Long paisId,Long monedaId)
    {
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.ubicacion= ubicacion;
        this.paisId = paisId;
        this.monedaId = monedaId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getPaisId() {
        return paisId;
    }

    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }

    public Long getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(Long monedaId) {
        this.monedaId = monedaId;
    }


    

}
