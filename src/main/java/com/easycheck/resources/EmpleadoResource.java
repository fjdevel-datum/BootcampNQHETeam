package com.easycheck.resources;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.dto.EmpleadoDTO;
import com.easycheck.repository.EmpleadoRepository;
import com.easycheck.service.IServiceEmpleado;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/empleado")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmpleadoResource {
    
    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    IServiceEmpleado  serviceEmpleado;


    @POST
    @Path("/crear")
    @Transactional
    public Response crearEmpleado(EmpleadoDTO dto)
    {
        try
        {
            EmpleadoDTO respuesta = serviceEmpleado.crearEmpleado(dto);
            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lista")
    public Response getEmpleado(){
        List<EmpleadoDTO> dtos = empleadoRepository.listAll().stream()
            .map(t -> new EmpleadoDTO(t.getEmpleadoId(),
            t.getNombres(),
            t.getApellidos(),
            t.getDocumentoIdentidad(),
            t.getEmpresa() != null ? t.getEmpresa().getEmpresaId() : null,
            t.getCentroCosto() != null ? t.getCentroCosto().getCentroId() : null))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }






}
