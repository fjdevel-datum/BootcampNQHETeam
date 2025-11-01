package com.easycheck.application.dto;

import java.util.Objects;

public class InformacionRecursoDTO {
    private Long empleadoId;
    private String nombreEmpleado;
    private String numeroTarjeta;
    private String fechaAsignada;
    private String estadoRecurso;
    private Double montoMaximo;
    private Double montoActual;
    private Double porcentaje;
    private Double resto;
    private Integer diaCorte;
    private String fechaExpiracion;
    private String tipoTarjeta;

    public InformacionRecursoDTO() {}

    public InformacionRecursoDTO(Long empleadoId, String nombreEmpleado, String numeroTarjeta,
                                 String fechaAsignada, String estadoRecurso, Double montoMaximo,
                                 Double montoActual, Double porcentaje, Double resto,
                                 Integer diaCorte, String fechaExpiracion, String tipoTarjeta) {
        this.empleadoId = empleadoId;
        this.nombreEmpleado = nombreEmpleado;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaAsignada = fechaAsignada;
        this.estadoRecurso = estadoRecurso;
        this.montoMaximo = montoMaximo;
        this.montoActual = montoActual;
        this.porcentaje = porcentaje;
        this.resto = resto;
        this.diaCorte = diaCorte;
        this.fechaExpiracion = fechaExpiracion;
        this.tipoTarjeta = tipoTarjeta;
    }

    // Getters y setters...
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaAsignada() {
        return fechaAsignada;
    }

    public void setFechaAsignada(String fechaAsignada) {
        this.fechaAsignada = fechaAsignada;
    }

    public String getEstadoRecurso() {
        return estadoRecurso;
    }

    public void setEstadoRecurso(String estadoRecurso) {
        this.estadoRecurso = estadoRecurso;
    }

    public Double getMontoMaximo() {
        return montoMaximo;
    }

    public void setMontoMaximo(Double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }

    public Double getMontoActual() {
        return montoActual;
    }

    public void setMontoActual(Double montoActual) {
        this.montoActual = montoActual;
    }

    public Double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Double getResto() {
        return resto;
    }

    public void setResto(Double resto) {
        this.resto = resto;
    }

    public Integer getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    // Métodos útiles que usan todos los campos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformacionRecursoDTO that = (InformacionRecursoDTO) o;
        return Objects.equals(empleadoId, that.empleadoId) &&
               Objects.equals(numeroTarjeta, that.numeroTarjeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empleadoId, numeroTarjeta);
    }

    @Override
    public String toString() {
        return "InformacionRecursoDTO{" +
                "empleadoId=" + empleadoId +
                ", nombreEmpleado='" + nombreEmpleado + '\'' +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", fechaAsignada='" + fechaAsignada + '\'' +
                ", estadoRecurso='" + estadoRecurso + '\'' +
                ", montoMaximo=" + montoMaximo +
                ", montoActual=" + montoActual +
                ", porcentaje=" + porcentaje +
                ", resto=" + resto +
                ", diaCorte=" + diaCorte +
                ", fechaExpiracion='" + fechaExpiracion + '\'' +
                ", tipoTarjeta='" + tipoTarjeta + '\'' +
                '}';
    }
}