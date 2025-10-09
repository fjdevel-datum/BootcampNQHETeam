package com.easycheck.model;


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
}

