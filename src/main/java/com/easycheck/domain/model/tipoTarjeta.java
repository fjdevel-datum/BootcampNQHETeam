package com.easycheck.domain.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TipoTarjeta")
public class tipoTarjeta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoId")
    private Long tipoId;

    @Column(name = "Tipo")
    private String tipo;

    @Column(name = "Descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "tipoTarjeta")
    private List<tarjeta> tarjetas;

    public tipoTarjeta() {
    }
    public tipoTarjeta(Long tipoId, String tipo, String descripcion) {
        this.tipoId = tipoId;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }





    
    public Long getTipoId() {
        return tipoId;
    }
    public void setTipoId(Long tipoId) {
        this.tipoId = tipoId;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public List<tarjeta> getTarjetas() {
        return tarjetas;
    }
    public void setTarjetas(List<tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }


    


}

