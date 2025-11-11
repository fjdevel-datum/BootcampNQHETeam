package com.easycheck.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.util.List;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Tarjeta")
public class tarjeta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TarjetaId")
    private Long tarjetaId;

    @ManyToOne
    @JoinColumn(name = "TipoId")
    private tipoTarjeta tipoTarjeta;

    @Column(name = "NumeroTarjeta")
    private String numeroTarjeta;

    @Column(name = "FechaExpiracion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaExpiracion;

    @Column(name = "Descripcion")
    private String descripcion;

    @Column(name = "DiaCorte")
    private Integer diaCorte;

    @OneToMany(mappedBy = "tarjeta")
    private List<recursoAsignado> recursosAsignados;

    public tarjeta() {
    }
    public tarjeta(Long tarjetaId, tipoTarjeta tipoTarjeta, String numeroTarjeta, Date fechaExpiracion, String descripcion, Integer diaCorte) {
        this.tarjetaId = tarjetaId;
        this.tipoTarjeta = tipoTarjeta;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaExpiracion = fechaExpiracion;
        this.descripcion = descripcion;
        this.diaCorte = diaCorte; 
    }
    public Long getTarjetaId() {
        return tarjetaId;
    }
    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }
    public tipoTarjeta getTipoTarjeta() {
        return tipoTarjeta;
    }
    public void setTipoTarjeta(tipoTarjeta tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
    public Date getFechaExpiracion() {
        return fechaExpiracion;
    }
    public void setFechaExpiracion(Date fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Integer getDiaCorte() {
        return diaCorte;
    }   
    public void setDiaCorte(Integer diaCorte) {
        this.diaCorte = diaCorte;
    }
    public List<recursoAsignado> getRecursosAsignados() {
        return recursosAsignados;
    }
    public void setRecursosAsignados(List<recursoAsignado> recursosAsignados) {
        this.recursosAsignados = recursosAsignados;
    }

    


}
