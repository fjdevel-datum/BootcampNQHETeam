package com.easycheck.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Moneda")
public class moneda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MonedaId")
    private Long monedaId;

    @Column(name = "CodigoISO", nullable = false, unique = true)
    private String codigoISO;

    @Column(name = "NombreMoneda")
    private String nombreMoneda;

    @Column(name = "Simbolo")
    private String simbolo;

    @OneToMany(mappedBy = "monedaBase")
    private List<empresa> empresaMonedaBase;

    @OneToMany(mappedBy = "monedaGasto")
    private List<gasto> gastos;

    // Getters y Setters
    public Long getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(Long monedaId) {
        this.monedaId = monedaId;
    }

    public String getCodigoISO() {
        return codigoISO;
    }

    public void setCodigoISO(String codigoISO) {
        this.codigoISO = codigoISO;
    }

    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public moneda() {
    }
    public moneda(Long monedaId, String codigoISO, String nombreMoneda, String simbolo) {
        this.monedaId = monedaId;
        this.codigoISO = codigoISO;
        this.nombreMoneda = nombreMoneda;
        this.simbolo = simbolo;
    }
}
