package com.easycheck.domain.service;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional; 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.easycheck.application.dto.tarjetaDTO;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.domain.model.tipoTarjeta;
import com.easycheck.infrastructure.repository.TarjetaRepository;
import com.easycheck.infrastructure.repository.tipoTarjetaRepository;

import java.time.ZoneId;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceTarjetaImp implements IServiceTarjeta {
    @Inject
    tipoTarjetaRepository tipoTarjetaRepository;

    @Inject
    TarjetaRepository tarjetaRepository;

    
    @Override
    @Transactional 
    public tarjetaDTO crearTarjeta(tarjetaDTO dto) throws IllegalArgumentException {
        
        tipoTarjeta tipo = tipoTarjetaRepository.findById(dto.getTipoId());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de tarjeta con ID " + dto.getTipoId() + " no encontrado.");
        }
        
        // Manejo y Validación de Fecha 
        Date fechaExpiracion;
        try {
            LocalDate localDate = LocalDate.parse(dto.getFechaExpiracion(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
             
            fechaExpiracion = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }

        // Creación y Persistencia (Modelo)
        tarjeta nueva = new tarjeta();
        nueva.setTipoTarjeta(tipo);
        nueva.setNumeroTarjeta(dto.getNumeroTarjeta());
        nueva.setFechaExpiracion(fechaExpiracion);
        nueva.setDescripcion(dto.getDescripcion());
        
        tarjetaRepository.persist(nueva);

            // Construir el DTO de respuesta
        tarjetaDTO respuesta = new tarjetaDTO(
            nueva.getTarjetaId(),
            tipo.getTipoId(),
            nueva.getNumeroTarjeta(),
            dto.getFechaExpiracion(),
            nueva.getDescripcion()
        );
        return respuesta;
    }
}
