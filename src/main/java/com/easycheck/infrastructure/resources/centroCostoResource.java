package com.easycheck.infrastructure.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.centroCostoDTO;
import com.easycheck.domain.model.centroCosto;
import com.easycheck.infrastructure.repository.centroCostoRepository;

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

@Path("/centroCosto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class centroCostoResource {


    @Inject
    centroCostoRepository crenCentroCostoRepository;

    
    @GET
    @Path("/lista")
    public Response getCentroCosto() {
        List<centroCostoDTO> dtos = crenCentroCostoRepository.listAll().stream()
            .map(t -> new centroCostoDTO(t.getCentroId(), t.getNombreCentro(),t.getDescripcion(),t.getResponsable()))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crearCentroCosto(centroCostoDTO dto){
        centroCosto  tCentro = new centroCosto();
        tCentro.setNombreCentro(dto.getNombreCentro());
        tCentro.setDescripcion(dto.getDescripcion());
        tCentro.setResponsable(dto.getResponsable());
        crenCentroCostoRepository.persist(tCentro);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarCentro(@PathParam("id")Long id, centroCostoDTO dto){
        centroCosto tCentro = crenCentroCostoRepository.findById(id);
        if(tCentro == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tCentro.setNombreCentro(dto.getNombreCentro());
        tCentro.setDescripcion(dto.getDescripcion());
        tCentro.setResponsable(dto.getResponsable());
        crenCentroCostoRepository.persist(tCentro);
        return Response.ok(new centroCosto(tCentro.getCentroId(),tCentro.getNombreCentro(),tCentro.getDescripcion(),tCentro.getResponsable())).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarCentro(@PathParam("id") Long Id){
        centroCosto tCentro = crenCentroCostoRepository.findById(Id);
        if( tCentro== null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        crenCentroCostoRepository.delete(tCentro);
        return Response.noContent().build();
    }




}
