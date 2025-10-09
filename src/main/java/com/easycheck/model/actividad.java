package com.easycheck.model;


import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Actividad")
public class actividad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ActividadId")
    private Long actividadId;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "FechaInicio")
    private Date fechaInicio;

    @Column(name = "FechaFinal")
    private Date fechaFinal;

    @Column(name = "Estado")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "EmpleadoId")
    private empleado empleado;

    @OneToMany(mappedBy = "actividad")
    private List<gasto> gastos;

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
    public Date getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    public Date getFechaFinal() {
        return fechaFinal;
    }
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(empleado empleado) {
        this.empleado = empleado;
    }
    public List<gasto> getGastos() {
        return gastos;
    }
    public void setGastos(List<gasto> gastos) {
        this.gastos = gastos;
    }
    public actividad() {
    }
    public actividad(Long actividadId, String nombre, Date fechaInicio, Date fechaFinal,String estado , empleado empleado) {
        this.actividadId = actividadId;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.estado = estado;
        this.empleado = empleado;
    }
}

