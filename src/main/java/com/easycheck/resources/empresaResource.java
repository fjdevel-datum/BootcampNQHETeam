package com.easycheck.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.dto.empresaDTO;
import com.easycheck.repository.empresaRepository;
import com.easycheck.service.IServiceEmpresa;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class empresaResource {

    @Inject
    empresaRepository empresaRepository;

    @Inject
    IServiceEmpresa serviceEmpresa;
    
    @POST
    @Path("/crear")
    @Transactional
    public Response crearEmpresa(empresaDTO dto)
    {
        try
        {
            empresaDTO respuesta = serviceEmpresa.crearEmpresa(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("/lista")
    @Transactional
    public Response getEmpresa(){
        List<empresaDTO> dtos = empresaRepository.listAll().stream()
            .map(t -> new empresaDTO(
                t.getEmpresaId(),
                t.getEmpresaNombre(),
                t.getUbicacion(),
                t.getPais() != null ? t.getPais().getPaisId() : null,
                t.getMonedaBase() != null ? t.getMonedaBase().getMonedaId() : null
            )).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }




}
