package com.easycheck.domain.service;

import com.easycheck.application.dto.tarjetaDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.domain.model.tipoTarjeta;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.TarjetaRepository;
import com.easycheck.infrastructure.repository.tipoTarjetaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceTarjetaImp implements IServiceTarjeta {

    @Inject
    TarjetaRepository tarjetaRepository;

    @Inject
    tipoTarjetaRepository tipoTarjetaRepository; 

    @Inject
    EmpleadoRepository empleadoRepository;

    @Override
    @Transactional
    public tarjetaDTO crearTarjeta(tarjetaDTO dto) throws IllegalArgumentException {
        
        System.out.println("Creando tarjeta...");
        
        // Validaciones
        if (dto.getNumeroTarjeta() == null || dto.getNumeroTarjeta().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de tarjeta es requerido");
        }

        if (dto.getTipoId() == null) {
            throw new IllegalArgumentException("El tipo de tarjeta es requerido");
        }

        // Verificar que el tipo de tarjeta existe
        tipoTarjeta tipo = tipoTarjetaRepository.findById(dto.getTipoId());
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de tarjeta no encontrado con ID: " + dto.getTipoId());
        }

        System.out.println("Tipo de tarjeta encontrado: " + tipo.getTipo());

        // Parsear fecha de expiración
        Date fechaExpiracion = null;
        if (dto.getFechaExpiracion() != null && !dto.getFechaExpiracion().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fechaExpiracion = sdf.parse(dto.getFechaExpiracion());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Formato de fecha incorrecto. Use yyyy-MM-dd");
            }
        }

        // Crear nueva tarjeta
        tarjeta nuevaTarjeta = new tarjeta();
        nuevaTarjeta.setTipoTarjeta(tipo);
        nuevaTarjeta.setNumeroTarjeta(dto.getNumeroTarjeta());
        nuevaTarjeta.setFechaExpiracion(fechaExpiracion);
        nuevaTarjeta.setDescripcion(dto.getDescripcion());
        nuevaTarjeta.setDiaCorte(dto.getDiaCorte());

        try {
            tarjetaRepository.persist(nuevaTarjeta);
            tarjetaRepository.flush();
            System.out.println("Tarjeta creada con ID: " + nuevaTarjeta.getTarjetaId());
        } catch (Exception e) {
            System.err.println("Error al persistir tarjeta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar la tarjeta en la base de datos", e);
        }

        // Retornar DTO
        return mapToDTO(nuevaTarjeta);
    }

    @Override
    public List<tarjetaDTO> obtenerTarjetasPorEmpleado(Long empleadoId) throws IllegalArgumentException {
        
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
        }

        // Verificar que el empleado existe
        empleado empleado = empleadoRepository.findById(empleadoId);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + empleadoId);
        }

        System.out.println("Buscando tarjetas para empleado: " + empleado.getNombres() + " " + empleado.getApellidos());

        // Obtener tarjetas del empleado a través del repository
        List<tarjeta> tarjetas = tarjetaRepository.findByUsuarioId(empleadoId);

        System.out.println("Tarjetas encontradas: " + tarjetas.size());

        // Mapear a DTO
        return tarjetas.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public tarjetaDTO obtenerTarjetaPorId(Long tarjetaId) throws IllegalArgumentException {
        
        if (tarjetaId == null) {
            throw new IllegalArgumentException("El ID de la tarjeta no puede ser nulo");
        }

        tarjeta tarjeta = tarjetaRepository.findById(tarjetaId);
        if (tarjeta == null) {
            throw new IllegalArgumentException("Tarjeta no encontrada con ID: " + tarjetaId);
        }

        return mapToDTO(tarjeta);
    }

    @Override
    public List<tarjetaDTO> listarTodasLasTarjetas() {
        
        System.out.println("Listando todas las tarjetas");
        
        List<tarjeta> tarjetas = tarjetaRepository.listAll();
        
        System.out.println("Total de tarjetas: " + tarjetas.size());
        
        return tarjetas.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public tarjetaDTO actualizarFechaExpiracion(Long tarjetaId, String fechaExpiracion) throws IllegalArgumentException {
        
        System.out.println("Actualizando fecha de expiración para tarjeta ID: " + tarjetaId);
        
        if (tarjetaId == null) {
            throw new IllegalArgumentException("El ID de la tarjeta no puede ser nulo");
        }

        // Buscar la tarjeta
        tarjeta tarjeta = tarjetaRepository.findById(tarjetaId);
        if (tarjeta == null) {
            throw new IllegalArgumentException("Tarjeta no encontrada con ID: " + tarjetaId);
        }

        // Parsear fecha de expiración si se proporciona
        Date nuevaFechaExpiracion = null;
        if (fechaExpiracion != null && !fechaExpiracion.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                nuevaFechaExpiracion = sdf.parse(fechaExpiracion);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Formato de fecha incorrecto. Use yyyy-MM-dd");
            }
        }

        // Actualizar solo la fecha de expiración
        tarjeta.setFechaExpiracion(nuevaFechaExpiracion);

        try {
            tarjetaRepository.persist(tarjeta);
            tarjetaRepository.flush();
            System.out.println("Fecha de expiración actualizada exitosamente");
        } catch (Exception e) {
            System.err.println("Error al actualizar fecha de expiración: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la fecha de expiración", e);
        }

        return mapToDTO(tarjeta);
    }

    /**
     * Mapea una entidad tarjeta a DTO
     */
    private tarjetaDTO mapToDTO(tarjeta tarjeta) {
        return new tarjetaDTO(
            tarjeta.getTarjetaId(),
            tarjeta.getTipoTarjeta() != null ? tarjeta.getTipoTarjeta().getTipoId() : null,
            tarjeta.getNumeroTarjeta(),
            tarjeta.getFechaExpiracion() != null ? 
                new SimpleDateFormat("yyyy-MM-dd").format(tarjeta.getFechaExpiracion()) : null,
            tarjeta.getDescripcion(),
            tarjeta.getDiaCorte()
        );
    }

}