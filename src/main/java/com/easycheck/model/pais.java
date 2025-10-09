package com.easycheck.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Pais")
public class pais implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PaisId")
    private Long paisId;

    @Column(name = "NombrePais")
    private String nombrePais;

    @Column(name = "CodigoPais")
    private String codigoPais;

    @OneToMany(mappedBy = "pais")
    private List<empresa> empresas;

    public pais() {
    }
    public pais(Long paisId, String nombrePais, String codigoPais) {
        this.paisId = paisId;
        this.nombrePais = nombrePais;
        this.codigoPais = codigoPais;
    }
    public Long getPaisId() {
        return paisId;
    }
    public void setPaisId(Long paisId) {
        this.paisId = paisId;
    }
    public String getNombrePais() {
        return nombrePais;
    }
    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }
    public String getCodigoPais() {
        return codigoPais;
    }
    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }
    public List<empresa> getEmpresas() {
        return empresas;
    }
    public void setEmpresas(List<empresa> empresas) {
        this.empresas = empresas;
    }

    


}

