package com.easycheck.application.dto;

public class FacturaDTO {
    private Long facturaId;
    private String comprobante;

    
    public FacturaDTO() {
    }
    public FacturaDTO(Long facturaId, String comprobante) {
        this.facturaId = facturaId;
        this.comprobante = comprobante;
        
    }
    public Long getFacturaId() {
        return facturaId;
    }
    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }
    public String getComprobante() {
        return comprobante;
    }
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
    
}
