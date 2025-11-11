package com.easycheck.application.dto;

public class PaisDTO {
    
    private Long paisId;
    private String nombrePais;
    private String codigoPais;
    
    public PaisDTO(){}

    public PaisDTO(Long paisId, String nombrePais, String codigoPais)
    {
        this.paisId = paisId;
        this.nombrePais = nombrePais;
        this.codigoPais = codigoPais;
    }

    public Long getPaisId() {
        return paisId;
    }

    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    
}
