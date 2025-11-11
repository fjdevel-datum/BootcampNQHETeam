package com.easycheck.domain.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "RecursoAsignado")
public class recursoAsignado  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecursoId")
    private Long recursoId;

    @ManyToOne
    @JoinColumn(name = "EmpleadoId")
    private empleado empleado;

    @ManyToOne
    @JoinColumn(name = "TarjetaId")
    private tarjeta tarjeta;

    @Column(name = "FechaAsignacion")
    private Date fechaAsignacion;

    @Column(name = "MontoMaximo")
    private Double montoMaximo;

    @Column(name = "Estado")
    private String estado;

    @OneToMany(mappedBy = "recursoAsignado")
    private List<gasto> gastos;

    @OneToMany(mappedBy = "recursoAsignado")
    private List<incidencia> incidencias;

    public recursoAsignado() {
    }
    public recursoAsignado(Long recursoId, empleado empleado, tarjeta tarjeta, Date fechaAsignacion, Double montoMaximo, String estado) {
        this.recursoId = recursoId;
        this.empleado = empleado;
        this.tarjeta = tarjeta;
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
    public empleado getEmpleado() {
        return empleado;
    }
    public void setEmpleado(empleado empleado) {
        this.empleado = empleado;
    }
    public tarjeta getTarjeta() {
        return tarjeta;
    }
    public void setTarjeta(tarjeta tarjeta) {
        this.tarjeta = tarjeta;
    }
    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }
    public void setFechaAsignacion(Date fechaAsignacion) {
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
    public List<gasto> getGastos() {
        return gastos;
    }
    public void setGastos(List<gasto> gastos) {
        this.gastos = gastos;
    }
    public List<incidencia> getIncidencias() {
        return incidencias;
    }
    public void setIncidencias(List<incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    



    
}

