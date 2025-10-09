package com.easycheck.resources;

import com.easycheck.dto.tipoGastoDTO;
import com.easycheck.repository.tipoGastoRepository;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import com.easycheck.model.tipoGasto;

@Path("/tipoGasto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class tipoGastoResourse {
    
    @Inject
    tipoGastoRepository tipoGastoRepository;

    @GET
    @Path("/lista")
    public Response getTiposGasto() {
        List<tipoGastoDTO> dtos = tipoGastoRepository.listAll().stream()
            .map(t -> new tipoGastoDTO(t.getTipoGastoId(), t.getNombre(), t.getDescripcion()))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crearTipoGasto(tipoGastoDTO dto){
        tipoGasto  tGasto = new tipoGasto();
        tGasto.setNombre(dto.getNombre());
        tGasto.setDescripcion(dto.getDescripcion());
        tipoGastoRepository.persist(tGasto);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarTipoGasto(@PathParam("id")Long id, tipoGastoDTO dto){
        tipoGasto tGasto = tipoGastoRepository.findById(id);
        if(tGasto == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tGasto.setNombre(dto.getNombre());
        tGasto.setDescripcion(dto.getDescripcion());
        tipoGastoRepository.persist(tGasto);
        return Response.ok(new tipoGastoDTO(tGasto.getTipoGastoId(),tGasto.getNombre(),tGasto.getDescripcion())).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarTipoGasto(@PathParam("id") Long Id){
        tipoGasto tGasto = tipoGastoRepository.findById(Id);
        if(tGasto == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tipoGastoRepository.delete(tGasto);
        return Response.noContent().build();
    }

}
