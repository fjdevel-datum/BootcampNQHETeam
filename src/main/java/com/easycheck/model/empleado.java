package com.easycheck.model;

import jakarta.persistence.*;
import java.util.List;

import java.io.Serializable;

@Entity
@Table(name = "Empleado")
public class empleado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmpleadoId")
    private Long empleadoId;

    @Column(name = "Nombres", nullable = false)
    private String nombres;

    @Column(name = "Apellidos", nullable = false)
    private String apellidos;

    @Column(name = "DocumentoIdentidad")
    private String documentoIdentidad;

    @ManyToOne
    @JoinColumn(name = "CentroCostoId")
    private centroCosto centroCosto;

    @ManyToOne
    @JoinColumn(name = "EmpresaId")
    private empresa empresa;

    @OneToMany(mappedBy = "empleado")
    private List<actividad> actividades;

    @OneToMany(mappedBy = "empleado")
    private List<recursoAsignado> recursosAsignados;

    @OneToMany(mappedBy = "empleado")
    private List<incidencia> incidencias;



    public empleado() {
    }

    public empleado(Long empleadoId, String nombres, String apellidos, String documentoIdentidad, centroCosto centroCosto, empresa empresa) {
        this.empleadoId = empleadoId;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documentoIdentidad = documentoIdentidad;
        this.centroCosto = centroCosto;
        this.empresa = empresa;
    }

    
    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public centroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(centroCosto centroCosto) {
        this.centroCosto = centroCosto;
    }

    public empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(empresa empresa) {
        this.empresa = empresa;
    }

    public List<actividad> getActividades() {
        return actividades;
    }

    public void setActividades(List<actividad> actividades) {
        this.actividades = actividades;
    }

    public List<recursoAsignado> getRecursosAsignados() {
        return recursosAsignados;
    }

    public void setRecursosAsignados(List<recursoAsignado> recursosAsignados) {
        this.recursosAsignados = recursosAsignados;
    }

    public List<incidencia> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<incidencia> incidencias) {
        this.incidencias = incidencias;
    }


}




