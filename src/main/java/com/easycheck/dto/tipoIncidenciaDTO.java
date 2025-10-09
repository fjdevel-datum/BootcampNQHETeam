package com.easycheck.dto;

public class tipoIncidenciaDTO {
    
    private Long tipoIncidenciaId;
    private String descripcion;

     public tipoIncidenciaDTO(){

    }

    public tipoIncidenciaDTO(Long tipoIncidenciaId, String descripcion)
    {
        this.tipoIncidenciaId=tipoIncidenciaId;
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

   
   
}
