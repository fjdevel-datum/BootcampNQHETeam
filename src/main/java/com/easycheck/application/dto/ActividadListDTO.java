package com.easycheck.application.dto;

public class ActividadListDTO {

    private Long actividadId;
    private String nombre;
    private String estado;
    private Long empleadoId;
    public ActividadListDTO(){}
    
    public ActividadListDTO(Long actividadId, String nombre, String estado, Long empleadoId)
    {
        this.actividadId = actividadId;
        this.nombre = nombre;
        this.estado= estado;
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
