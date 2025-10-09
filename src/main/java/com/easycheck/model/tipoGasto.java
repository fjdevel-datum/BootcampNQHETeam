package com.easycheck.model;

import java.io.Serializable;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "TipoGasto")
public class tipoGasto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoGastoId")
    private Long tipoGastoId;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "tipoGasto")
    private List<gasto> gastos;

    public tipoGasto() {
    }
    public tipoGasto(Long tipoGastoId, String nombre, String descripcion) {
        this.tipoGastoId = tipoGastoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public Long getTipoGastoId() {
        return tipoGastoId;
    }
    public void setTipoGastoId(Long tipoGastoId) {
        this.tipoGastoId = tipoGastoId;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public List<gasto> getGastos() {
        return gastos;
    }
    public void setGastos(List<gasto> gastos) {
        this.gastos = gastos;
    }

    
}
