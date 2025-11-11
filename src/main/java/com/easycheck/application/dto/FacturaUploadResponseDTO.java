package com.easycheck.application.dto;

public class FacturaUploadResponseDTO {
    
    private Long facturaId;
    private String comprobanteUrl;

    public FacturaUploadResponseDTO(Long facturaId, String comprobanteUrl) {
        this.facturaId = facturaId;
        this.comprobanteUrl = comprobanteUrl;
    }
    
    // Getters y Setters
    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }
}