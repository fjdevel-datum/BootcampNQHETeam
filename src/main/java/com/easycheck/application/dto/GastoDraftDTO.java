package com.easycheck.application.dto;

public class GastoDraftDTO {
    
    private String draftId;
    private Long monedaGasto;
    private Long facturaId;
    private Long recursoId;
    private Long tipoGastoId;
    private Long actividadId;
    private Double totalMonedaBase;

    public GastoDraftDTO(){}
    public GastoDraftDTO(
        String draftId,
     Long monedaGasto,
     Long facturaId,
     Long recursoId,
     Long tipoGastoId,
     Long actividadId,
     Double totalMonedaBase
    )
    {
        this.draftId = draftId;
        this.monedaGasto = monedaGasto;
        this.facturaId = facturaId;
        this.recursoId = recursoId;
        this.tipoGastoId = tipoGastoId;
        this.actividadId = actividadId;
        this.totalMonedaBase = totalMonedaBase;
    }
    public String getDraftId() {
        return draftId;
    }
    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }
    public Long getMonedaGasto() {
        return monedaGasto;
    }
    public void setMonedaGasto(Long monedaGasto) {
        this.monedaGasto = monedaGasto;
    }
    public Long getFacturaId() {
        return facturaId;
    }
    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }
    public Long getRecursoId() {
        return recursoId;
    }
    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }
    public Long getTipoGastoId() {
        return tipoGastoId;
    }
    public void setTipoGastoId(Long tipoGastoId) {
        this.tipoGastoId = tipoGastoId;
    }
    public Long getActividadId() {
        return actividadId;
    }
    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }
    public Double getTotalMonedaBase() {
        return totalMonedaBase;
    }
    public void setTotalMonedaBase(Double totalMonedaBase) {
        this.totalMonedaBase = totalMonedaBase;
    }
    
}
