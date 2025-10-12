package com.easycheck.application.dto;



public class GastoDTO {

    private Long gastoId;
    private String fecha;
    private Long monedaGasto;
    private Long facturaId;
    private Long recursoId;
    private Long tipoGastoId;
    private Long actividadId;
    private String descripcionGasto;
    private double totalGasto;
    private Double totalMonedaBase;

     public GastoDTO() {
    }
    public GastoDTO(Long gastoId, String fecha, Long monedaGasto, Long facturaId, Long recursoId, Long tipoGastoId, Long actividadId, String descripcionGasto, Double totalGasto, Double totalMonedaBase) {
        this.gastoId = gastoId;
        this.fecha = fecha;
        this.monedaGasto = monedaGasto;
        this.facturaId = facturaId;
        this.recursoId = recursoId;
        this.tipoGastoId = tipoGastoId;
        this.actividadId = actividadId;
        this.descripcionGasto = descripcionGasto;
        this.totalGasto = totalGasto;
        this.totalMonedaBase = totalMonedaBase;
    }
    public Long getGastoId() {
        return gastoId;
    }
    public void setGastoId(Long gastoId) {
        this.gastoId = gastoId;
    }
    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
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
    public String getDescripcionGasto() {
        return descripcionGasto;
    }
    public void setDescripcionGasto(String descripcionGasto) {
        this.descripcionGasto = descripcionGasto;
    }
    public double getTotalGasto() {
        return totalGasto;
    }
    public void setTotalGasto(double totalGasto) {
        this.totalGasto = totalGasto;
    }
    public Double getTotalMonedaBase() {
        return totalMonedaBase;
    }
    public void setTotalMonedaBase(Double totalMonedaBase) {
        this.totalMonedaBase = totalMonedaBase;
    }
    
}
