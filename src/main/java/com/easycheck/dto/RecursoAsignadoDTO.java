package com.easycheck.dto;




public class RecursoAsignadoDTO {

    private Long recursoId;
    private Long empleadoId;
    private Long tarjetaId;
    private String fechaAsignacion;
    private Double montoMaximo;
    private String estado;

      public RecursoAsignadoDTO() {
    }
    public RecursoAsignadoDTO(Long recursoId, Long empleadoId, Long tarjetaId, String fechaAsignacion, Double montoMaximo, String estado) {
        this.recursoId = recursoId;
        this.empleadoId = empleadoId;
        this.tarjetaId = tarjetaId;
        this.fechaAsignacion = fechaAsignacion;
        this.montoMaximo = montoMaximo;
        this.estado = estado;
    }
    public Long getRecursoId() {
        return recursoId;
    }
    public void setRecursoId(Long recursoId) {
        this.recursoId = recursoId;
    }
    public Long getEmpleadoId() {
        return empleadoId;
    }
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }
    public Long getTarjetaId() {
        return tarjetaId;
    }
    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }
    public String getFechaAsignacion() {
        return fechaAsignacion;
    }
    public void setFechaAsignacion(String fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
    public Double getMontoMaximo() {
        return montoMaximo;
    }
    public void setMontoMaximo(Double montoMaximo) {
        this.montoMaximo = montoMaximo;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    


}
