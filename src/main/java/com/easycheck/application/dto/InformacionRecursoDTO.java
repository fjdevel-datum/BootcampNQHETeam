package com.easycheck.application.dto;

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

    public InformacionRecursoDTO() {}

    public InformacionRecursoDTO(Long empleadoId, String nombreEmpleado, String numeroTarjeta,
                                 String fechaAsignada, String estadoRecurso, Double montoMaximo,
                                 Double montoActual, Double porcentaje, Double resto) {
        this.empleadoId = empleadoId;
        this.nombreEmpleado = nombreEmpleado;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaAsignada = fechaAsignada;
        this.estadoRecurso = estadoRecurso;
        this.montoMaximo = montoMaximo;
        this.montoActual = montoActual;
        this.porcentaje = porcentaje;
        this.resto = resto;
    }

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


}
