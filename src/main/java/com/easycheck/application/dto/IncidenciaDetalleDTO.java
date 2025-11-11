package com.easycheck.application.dto;

public class IncidenciaDetalleDTO {
    private Long incidenciaId;
    private Long empleadoId;
    private String nombreEmpleado;
    private Long tipoIncidenciaId;
    private String tipoIncidenciaNombre;
    private Long recursoId;
    private String numeroTarjeta;
    private String fechaIncidencia;
    private String descripcion;

    public IncidenciaDetalleDTO() {}

    public IncidenciaDetalleDTO(Long incidenciaId, Long empleadoId, String nombreEmpleado,
                                Long tipoIncidenciaId, String tipoIncidenciaNombre,
                                Long recursoId, String numeroTarjeta,
                                String fechaIncidencia, String descripcion) {
        this.incidenciaId = incidenciaId;
        this.empleadoId = empleadoId;
        this.nombreEmpleado = nombreEmpleado;
        this.tipoIncidenciaId = tipoIncidenciaId;
        this.tipoIncidenciaNombre = tipoIncidenciaNombre;
        this.recursoId = recursoId;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaIncidencia = fechaIncidencia;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Long getIncidenciaId() {
        return incidenciaId;
    }

    public void setIncidenciaId(Long incidenciaId) {
        this.incidenciaId = incidenciaId;
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

    public Long getTipoIncidenciaId() {
        return tipoIncidenciaId;
    }

    public void setTipoIncidenciaId(Long tipoIncidenciaId) {
        this.tipoIncidenciaId = tipoIncidenciaId;
    }

    public String getTipoIncidenciaNombre() {
        return tipoIncidenciaNombre;
    }

    public void setTipoIncidenciaNombre(String tipoIncidenciaNombre) {
        this.tipoIncidenciaNombre = tipoIncidenciaNombre;
    }

    public Long getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaIncidencia() {
        return fechaIncidencia;
    }

    public void setFechaIncidencia(String fechaIncidencia) {
        this.fechaIncidencia = fechaIncidencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}