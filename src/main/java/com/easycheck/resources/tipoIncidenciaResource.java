package com.easycheck.resources;

import com.easycheck.dto.tipoIncidenciaDTO;
import com.easycheck.model.tipoIncidencia;
import com.easycheck.repository.tipoIncidenciaRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;




@Path("/tipoIncidencia")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class tipoIncidenciaResource {

    @Inject 
    tipoIncidenciaRepository tipoIncidenciaRepository;

    @GET
    @Path("/lista")
    public Response getTipoIncidencia()
    {
        List<tipoIncidenciaDTO> dtos = tipoIncidenciaRepository.listAll().stream()
            .map(t -> new tipoIncidenciaDTO(t.getTipoIncidenciaId(),t.getDescripcion()))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

     @POST
    @Path("/crear")
    @Transactional
    public Response crearTipoIncidencia(tipoIncidenciaDTO dto){
        tipoIncidencia  tTipoIncidencia = new tipoIncidencia();
        tTipoIncidencia.setDescripcion(dto.getDescripcion());
        tipoIncidenciaRepository.persist(tTipoIncidencia);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarTipoIncidencia(@PathParam("id")Long id, tipoIncidenciaDTO dto){
        tipoIncidencia tTipoIncidencia = tipoIncidenciaRepository.findById(id);
        if(tTipoIncidencia == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tTipoIncidencia.setDescripcion(dto.getDescripcion());
        tipoIncidenciaRepository.persist(tTipoIncidencia);
        return Response.ok(new tipoIncidenciaDTO(tTipoIncidencia.getTipoIncidenciaId(),tTipoIncidencia.getDescripcion())).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarTipoGasto(@PathParam("id") Long Id){
        tipoIncidencia tTipoIncidencia = tipoIncidenciaRepository.findById(Id);
        if(tTipoIncidencia == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tipoIncidenciaRepository.delete(tTipoIncidencia);
        return Response.noContent().build();
    }
    



}
