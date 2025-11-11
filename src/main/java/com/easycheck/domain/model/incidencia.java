package com.easycheck.domain.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Incidencia")
public class incidencia implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IncidenciaId")
    private Long incidenciaId;

    @ManyToOne
    @JoinColumn(name = "EmpleadoId")
    private empleado empleado;

    @ManyToOne
    @JoinColumn(name = "TipoIncidenciaId")
    private tipoIncidencia tipoIncidencia;

    @ManyToOne
    @JoinColumn(name = "RecursoId")
    private recursoAsignado recursoAsignado;

    @Column(name = "FechaIncidencia")
    private Date fechaIncidencia;

    @Column(name = "Descripcion")
    private String descripcion;

    public incidencia() {
    }
    public incidencia(Long incidenciaId, empleado empleado, tipoIncidencia tipoIncidencia, recursoAsignado recursoAsignado, Date fechaIncidencia, String descripcion) {
        this.incidenciaId = incidenciaId;
        this.empleado = empleado;
        this.tipoIncidencia = tipoIncidencia;
        this.recursoAsignado = recursoAsignado;
        this.fechaIncidencia = fechaIncidencia;
        this.descripcion = descripcion;
    }
    public Long getIncidenciaId() {
        return incidenciaId;
    }
    public void setIncidenciaId(Long incidenciaId) {
        this.incidenciaId = incidenciaId;
    }
    public empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(empleado empleado) {
        this.empleado = empleado;
    }
    public tipoIncidencia getTipoIncidencia() {
        return tipoIncidencia;
    }
    public void setTipoIncidencia(tipoIncidencia tipoIncidencia) {
        this.tipoIncidencia = tipoIncidencia;
    }
    public recursoAsignado getRecursoAsignado() {
        return recursoAsignado;
    }
    public void setRecursoAsignado(recursoAsignado recursoAsignado) {
        this.recursoAsignado = recursoAsignado;
    }
    public Date getFechaIncidencia() {
        return fechaIncidencia;
    }
    public void setFechaIncidencia(Date fechaIncidencia) {
        this.fechaIncidencia = fechaIncidencia;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    
}

