package com.easycheck.resources;

import java.util.List;

import com.easycheck.dto.PaisDTO;
import com.easycheck.model.pais;
import com.easycheck.repository.PaisRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;



@Path("/Pais")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaisResource {

    @Inject
    PaisRepository paisRepository;

    @GET
    @Path("/lista")
    public Response getPais()
    {        
        List<PaisDTO> pais = paisRepository.listAll().stream()
            .map(t-> new PaisDTO(t.getPaisId(),t.getNombrePais(),t.getCodigoPais()))
            .collect(Collectors.toList());
        return Response.ok(pais).build();

    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crearPais(PaisDTO paisdto)
    {
        pais tpais = new pais();
        tpais.setNombrePais(paisdto.getNombrePais());
        tpais.setCodigoPais(paisdto.getCodigoPais());
        paisRepository.persist(tpais);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarPais(@PathParam("id")Long id, PaisDTO dto){
        pais tpais = paisRepository.findById(id);
        if(tpais == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tpais.setNombrePais(dto.getNombrePais());
        tpais.setCodigoPais(dto.getCodigoPais());
        paisRepository.persist(tpais);
        return Response.ok(new PaisDTO(tpais.getPaisId(),tpais.getNombrePais(),tpais.getCodigoPais())).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarPais(@PathParam("id") Long Id){
        pais tpais = paisRepository.findById(Id);
        if(tpais == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        paisRepository.delete(tpais);
        return Response.noContent().build();
    }


    
}
