package com.easycheck.application.dto;



public class IncidenciaDTO {

    private Long incidenciaId;
    private long empleadoId;
    private Long tipoIncidenciaId;
    private Long recursoId;
    private String fechaIncidencia;
    private String descripcion;


     public IncidenciaDTO() {
    }
    public IncidenciaDTO(Long incidenciaId, Long empleadoId, Long tipoIncidenciaId, Long recursoId, String fechaIncidencia, String descripcion) {
        this.incidenciaId = incidenciaId;
        this.empleadoId = empleadoId;
        this.tipoIncidenciaId = tipoIncidenciaId;
        this.recursoId = recursoId;
        this.fechaIncidencia = fechaIncidencia;
        this.descripcion = descripcion;
    }
    public Long getIncidenciaId() {
        return incidenciaId;
    }
    public void setIncidenciaId(Long incidenciaId) {
        this.incidenciaId = incidenciaId;
    }
    public long getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(long empleadoId) {
        this.empleadoId = empleadoId;
    }
    public Long getTipoIncidenciaId() {
        return tipoIncidenciaId;
    }
    public void setTipoIncidenciaId(Long tipoIncidenciaId) {
        this.tipoIncidenciaId = tipoIncidenciaId;
    }
    public Long getRecursoId() {
        return recursoId;
    }
    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
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
