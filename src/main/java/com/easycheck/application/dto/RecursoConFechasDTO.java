package com.easycheck.application.dto;

import java.util.Objects;

/**
 * DTO simple para listar recursos con fechas calculadas para reportes
 */
public class RecursoConFechasDTO {
    private Long recursoId;
    private Long tarjetaId;
    private String numeroTarjeta;
    private String descripcionTarjeta;
    private String tipoTarjeta;
    private String estadoRecurso;
    
    // Fechas calculadas para pasar al endpoint de Excel
    private String fechaInicioReporte;
    private String fechaFinalReporte;
    
    // Datos adicionales para contexto
    private Integer diaCorte;
    private String fechaAsignacion;
    private String fechaExpiracion;

    public RecursoConFechasDTO() {}

    public RecursoConFechasDTO(Long recursoId, Long tarjetaId, String numeroTarjeta,
                               String descripcionTarjeta, String tipoTarjeta, String estadoRecurso,
                               String fechaInicioReporte, String fechaFinalReporte,
                               Integer diaCorte, String fechaAsignacion, String fechaExpiracion) {
        this.recursoId = recursoId;
        this.tarjetaId = tarjetaId;
        this.numeroTarjeta = numeroTarjeta;
        this.descripcionTarjeta = descripcionTarjeta;
        this.tipoTarjeta = tipoTarjeta;
        this.estadoRecurso = estadoRecurso;
        this.fechaInicioReporte = fechaInicioReporte;
        this.fechaFinalReporte = fechaFinalReporte;
        this.diaCorte = diaCorte;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaExpiracion = fechaExpiracion;
    }

    // Getters y Setters
    public Long getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }

    public Long getTarjetaId() {
        return tarjetaId;
    }

    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getDescripcionTarjeta() {
        return descripcionTarjeta;
    }

    public void setDescripcionTarjeta(String descripcionTarjeta) {
        this.descripcionTarjeta = descripcionTarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getEstadoRecurso() {
        return estadoRecurso;
    }

    public void setEstadoRecurso(String estadoRecurso) {
        this.estadoRecurso = estadoRecurso;
    }

    public String getFechaInicioReporte() {
        return fechaInicioReporte;
    }

    public void setFechaInicioReporte(String fechaInicioReporte) {
        this.fechaInicioReporte = fechaInicioReporte;
    }

    public String getFechaFinalReporte() {
        return fechaFinalReporte;
    }

    public void setFechaFinalReporte(String fechaFinalReporte) {
        this.fechaFinalReporte = fechaFinalReporte;
    }

    public Integer getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }

    public String getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecursoConFechasDTO that = (RecursoConFechasDTO) o;
        return Objects.equals(recursoId, that.recursoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recursoId);
    }

    @Override
    public String toString() {
        return "RecursoConFechasDTO{" +
                "recursoId=" + recursoId +
                ", tarjetaId=" + tarjetaId +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", tipoTarjeta='" + tipoTarjeta + '\'' +
                ", fechaInicioReporte='" + fechaInicioReporte + '\'' +
                ", fechaFinalReporte='" + fechaFinalReporte + '\'' +
                '}';
    }
}