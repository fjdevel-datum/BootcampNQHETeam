package com.easycheck.domain.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.checkerframework.checker.units.qual.g;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.factura;
import com.easycheck.domain.model.gasto;
import com.easycheck.domain.model.moneda;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tipoGasto;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.easycheck.infrastructure.repository.GastoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.monedaRepository;
import com.easycheck.infrastructure.repository.tipoGastoRepository;
import jakarta.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class ServiceGastoImp implements IServiceGasto {
    
    @Inject
    RecursoAsignadoRepository recursoAsignadoRepository;

    @Inject
    ActividadRepository actividadRepository;

    @Inject
    tipoGastoRepository tipoGastoRepository;

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    monedaRepository monedaRepository;

    @Inject
    GastoRepository gastoRepository;


    @Override
    public GastoDTO crearGasto(GastoDTO gasto)throws IllegalArgumentException
    {
        //validamos
        moneda moneda = monedaRepository.findById(gasto.getMonedaGasto());
        if(moneda==null)
        {
            throw new IllegalArgumentException("Moneda no encontrada");
        }
        factura factura = facturaRepository.findById(gasto.getFacturaId());
        if(factura==null)
        {
            throw new IllegalArgumentException("Factura no encontrada");
        }
        recursoAsignado recurso = recursoAsignadoRepository.findById(gasto.getRecursoId());
        if(recurso==null)
        {
            throw new IllegalArgumentException("Recurso no encontrado");
        }
        tipoGasto tipoGasto = tipoGastoRepository.findById(gasto.getTipoGastoId());
        if(tipoGasto==null)
        {
            throw new IllegalArgumentException("Tipo de gasto no encontrado");
        }
        actividad actividad = actividadRepository.findById(gasto.getActividadId());
        if(actividad==null)
        {
            throw new IllegalArgumentException("Actividad no encontrada");
        }

        Date fechagasto;
        try {
            LocalDate localDate = LocalDate.parse(gasto.getFecha(),DateTimeFormatter.ofPattern("yyy-MM-dd"));
            fechagasto = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }

        gasto nuevoGasto = new gasto();
        nuevoGasto.setFecha(fechagasto);
        nuevoGasto.setMonedaGasto(moneda);
        nuevoGasto.setFactura(factura);
        nuevoGasto.setRecursoAsignado(recurso);
        nuevoGasto.setTipoGasto(tipoGasto);
        nuevoGasto.setActividad(actividad);
        nuevoGasto.setDescripcionGasto(gasto.getDescripcionGasto());
        nuevoGasto.setTotalGasto(gasto.getTotalGasto());
        nuevoGasto.setTotalMonedaBase(gasto.getTotalMonedaBase());

        gastoRepository.persist(nuevoGasto);

        GastoDTO gastoDTO = new GastoDTO(
            nuevoGasto.getGastoId(),
            gasto.getFecha(),
            moneda.getMonedaId(),
            factura.getFacturaId(),
            recurso.getRecursoId(),
            tipoGasto.getTipoGastoId(),
            actividad.getActividadId(),
            nuevoGasto.getDescripcionGasto(),
            nuevoGasto.getTotalGasto(),
            nuevoGasto.getTotalMonedaBase()
        );
        return gastoDTO;




    }
    




}
