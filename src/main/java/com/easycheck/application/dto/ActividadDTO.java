package com.easycheck.application.dto;



public class ActividadDTO {
    
    private Long actividadId;
    private String nombre;
    private String fechaInicio;
    private String fechaFinal;
    private String estado;
    private Long empleadoId;

      public ActividadDTO() {
    }
    public ActividadDTO(Long actividadId, String nombre, String fechaInicio, String fechaFinal,String estado , Long empleadoId) {
        this.actividadId = actividadId;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.estado = estado;
        this.empleadoId = empleadoId;
    }
    public Long getActividadId() {
        return actividadId;
    }
    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public String getFechaFinal() {
        return fechaFinal;
    }
    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Long getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }
    
}
