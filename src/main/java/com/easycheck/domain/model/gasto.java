package com.easycheck.domain.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Gasto")
public class gasto implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GastoId")
    private Long gastoId;

    @Column(name = "Fecha")
    private Date fecha;

       // Relación con la moneda específica del gasto
    @ManyToOne
    @JoinColumn(name = "MonedaId", nullable = false)
    private moneda monedaGasto;

    @ManyToOne
    @JoinColumn(name = "FacturaId")
    private factura factura;

    @ManyToOne
    @JoinColumn(name = "RecursoId")
    private recursoAsignado recursoAsignado;

    @ManyToOne
    @JoinColumn(name = "TipoGastoId")
    private tipoGasto tipoGasto;

    @ManyToOne
    @JoinColumn(name = "ActividadId")
    private actividad actividad;

    @Column(name = "DescripcionGasto")
    private String descripcionGasto;

    @Column(name = "TotalGasto")
    private Double totalGasto;

    // Monto convertido a la moneda base definida en Empresa
    @Column(name = "TotalMonedaBase")
    private Double totalMonedaBase;

    public gasto() {
    }
    public gasto(Long gastoId, Date fecha, moneda monedaGasto, factura factura, recursoAsignado recursoAsignado, tipoGasto tipoGasto, actividad actividad, String descripcionGasto, Double totalGasto, Double totalMonedaBase) {
        this.gastoId = gastoId;
        this.fecha = fecha;
        this.monedaGasto = monedaGasto;
        this.factura = factura;
        this.recursoAsignado = recursoAsignado;
        this.tipoGasto = tipoGasto;
        this.actividad = actividad;
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
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public moneda getMonedaGasto() {
        return monedaGasto;
    }
    public void setMonedaGasto(moneda monedaGasto) {
        this.monedaGasto = monedaGasto;
    }
    public factura getFactura() {
        return factura;
    }
    public void setFactura(factura factura) {
        this.factura = factura;
    }
    public recursoAsignado getRecursoAsignado() {
        return recursoAsignado;
    }
    public void setRecursoAsignado(recursoAsignado recursoAsignado) {
        this.recursoAsignado = recursoAsignado;
    }
    public tipoGasto getTipoGasto() {
        return tipoGasto;
    }
    public void setTipoGasto(tipoGasto tipoGasto) {
        this.tipoGasto = tipoGasto;
    }
    public actividad getActividad() {
        return actividad;
    }
    public void setActividad(actividad actividad) {
        this.actividad = actividad;
    }
    public String getDescripcionGasto() {
        return descripcionGasto;
    }
    public void setDescripcionGasto(String descripcionGasto) {
        this.descripcionGasto = descripcionGasto;
    }
    public Double getTotalGasto() {
        return totalGasto;
    }
    public void setTotalGasto(Double totalGasto) {
        this.totalGasto = totalGasto;
    }
    public Double getTotalMonedaBase() {
        return totalMonedaBase;
    }
    public void setTotalMonedaBase(Double totalMonedaBase) {
        this.totalMonedaBase = totalMonedaBase;
    }

    

}
