package com.easycheck.model;


import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Empresa")
public class empresa implements Serializable {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmpresaId")
    private Long empresaId;

    @Column(name = "EmpresaNombre", nullable = false)
    private String empresaNombre;

    @Column(name = "Ubicacion")
    private String ubicacion;

    @ManyToOne
    @JoinColumn(name = "PaisId")
    private pais pais;

    @ManyToOne
    @JoinColumn(name = "MonedaId", nullable = false)
    private moneda monedaBase;

    @OneToMany(mappedBy = "empresa")
    private List<empleado> empleados;

    public empresa() {
    }
    public empresa(Long empresaId, String empresaNombre, String ubicacion, pais pais, moneda monedaBase) {
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.ubicacion = ubicacion;
        this.pais = pais;
        this.monedaBase = monedaBase;
    }

    
    public Long getEmpresaId() {
        return empresaId;
    }
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
    public String getEmpresaNombre() {
        return empresaNombre;
    }
    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }
    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public pais getPais() {
        return pais;
    }
    public void setPais(pais pais) {
        this.pais = pais;
    }
    public moneda getMonedaBase() {
        return monedaBase;
    }
    public void setMonedaBase(moneda monedaBase) {
        this.monedaBase = monedaBase;
    }
    public List<empleado> getEmpleados() {
        return empleados;
    }
    public void setEmpleados(List<empleado> empleados) {
        this.empleados = empleados;
    }
}
