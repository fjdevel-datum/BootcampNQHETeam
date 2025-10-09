package com.easycheck.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "CentroCosto")
public class centroCosto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CentroId")
    private Long centroId;

    @Column(name = "NombreCentro", nullable = false)
    private String nombreCentro;

    @Column(name = "Descripcion")
    private String descripcion;

    @Column(name = "Responsable")
    private String responsable;

    public Long getCentroId() {
        return centroId;
    }

    public void setCentroId(Long centroId) {
        this.centroId = centroId;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public List<empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<empleado> empleados) {
        this.empleados = empleados;
    }

    @OneToMany(mappedBy = "centroCosto")
    private List<empleado> empleados;



    public centroCosto() {
    }

    public centroCosto(Long centroId, String nombreCentro, String descripcion, String responsable) {
        this.centroId = centroId;
        this.nombreCentro = nombreCentro;
        this.descripcion = descripcion;
        this.responsable = responsable;
    }


}
 
