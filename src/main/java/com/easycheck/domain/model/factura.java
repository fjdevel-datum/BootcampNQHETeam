package com.easycheck.domain.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Factura")
public class factura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FacturaId")
    private Long facturaId;

    @Column(name = "URLComprobante")
    private String comprobante;

    @OneToMany(mappedBy = "factura")
    private List<gasto> gastos;

    public factura() {
    }
    public factura(Long facturaId, String comprobante) {
        this.facturaId = facturaId;
        this.comprobante = comprobante;
        
    }
    public Long getFacturaId() {
        return facturaId;
    }
    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }
    public String getComprobante() {
        return comprobante;
    }
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
    public List<gasto> getGastos() {
        return gastos;
    }
    public void setGastos(List<gasto> gastos) {
        this.gastos = gastos;
    }
    
}

