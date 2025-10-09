package com.easycheck.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "TipoIncidencia")
public class tipoIncidencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipoIncidenciaId")
    private Long tipoIncidenciaId;

    @Column(name = "Descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "tipoIncidencia")
    private List<incidencia> incidencias;

    public tipoIncidencia() {
    }
    public tipoIncidencia(Long tipoIncidenciaId, String descripcion) {
        this.tipoIncidenciaId = tipoIncidenciaId;
        this.descripcion = descripcion;
    }


    
    public Long getTipoIncidenciaId() {
        return tipoIncidenciaId;
    }
    public void setTipoIncidenciaId(Long tipoIncidenciaId) {
        this.tipoIncidenciaId = tipoIncidenciaId;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public List<incidencia> getIncidencias() {
        return incidencias;
    }
    public void setIncidencias(List<incidencia> incidencias) {
        this.incidencias = incidencias;
    }

    



}

