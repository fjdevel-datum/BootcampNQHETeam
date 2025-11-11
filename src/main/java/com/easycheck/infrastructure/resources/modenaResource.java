package com.easycheck.infrastructure.resources;

import jakarta.ws.rs.Produces;

import java.util.List;
import java.util.stream.Collectors;

import com.easycheck.application.dto.monedaDTO;
import com.easycheck.domain.model.moneda;
import com.easycheck.infrastructure.repository.monedaRepository;
import com.easycheck.domain.service.IServiceMoneda;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.Map;


@Path("/moneda")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class modenaResource {

    @Inject
    monedaRepository monedaRepository;

    @Inject
    IServiceMoneda serviceMoneda;

    @GET
    @Path("/lista")
    public Response getMonedas()
    {
        List<monedaDTO> dtos = monedaRepository.listAll().stream()
            .map(t -> new monedaDTO(t.getMonedaId(), t.getCodigoISO(),t.getNombreMoneda(),t.getSimbolo()))
            .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @POST
    @Path("/crear")
    @Transactional
    public Response crearMoneda(monedaDTO dto){
        moneda  tmoneda = new moneda();
        tmoneda.setCodigoISO(dto.getCodigoISO());
        tmoneda.setNombreMoneda(dto.getNombreMoneda());
        tmoneda.setSimbolo(dto.getSimbolo());
        monedaRepository.persist(tmoneda);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/actualizar/{id}")
    @Transactional
    public Response actualizarMoneda(@PathParam("id")Long id, monedaDTO dto){
        moneda tmoneda = monedaRepository.findById(id);
        if(tmoneda == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tmoneda.setCodigoISO(dto.getCodigoISO());
        tmoneda.setNombreMoneda(dto.getNombreMoneda());
        tmoneda.setSimbolo(dto.getSimbolo());
        monedaRepository.persist(tmoneda);
        return Response.ok(new moneda(tmoneda.getMonedaId(),tmoneda.getCodigoISO(),tmoneda.getNombreMoneda(),tmoneda.getSimbolo())).build();
    }

    @DELETE
    @Path("/eliminar/{id}")
    @Transactional
    public Response eliminarMoneda(@PathParam("id") Long Id){
        moneda tMoneda = monedaRepository.findById(Id);
        if( tMoneda== null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        monedaRepository.delete(tMoneda);
        return Response.noContent().build();
    }

    @GET
    @Path("/convertir")
    public Response convertirMoneda(
            @QueryParam("empleadoId") Long empleadoId,
            @QueryParam("monedaGastoCodigo") String monedaGastoCodigo,
            @QueryParam("monto") Double monto) {

        if (empleadoId == null || monedaGastoCodigo == null || monto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Debe enviar empleadoId, monedaGastoCodigo y monto"))
                    .build();
        }

        Double montoConvertido = serviceMoneda.convertirAMonedaBase(empleadoId, monedaGastoCodigo, monto);

        if (montoConvertido == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "No se pudo realizar la conversión"))
                    .build();
        }

        return Response.ok(Map.of(
                "montoOriginal", monto,
                "monedaGasto", monedaGastoCodigo,
                "montoMonedaBase", montoConvertido
        )).build();
    }






    
}
