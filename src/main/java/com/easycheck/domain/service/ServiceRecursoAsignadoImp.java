package com.easycheck.domain.service;

import com.easycheck.application.dto.InformacionRecursoDTO;
import com.easycheck.application.dto.RecursoAsignadoDTO;
import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.TarjetaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceRecursoAsignadoImp implements IServiceRecursoAsignado {

    @Inject
    RecursoAsignadoRepository recursoAsignadoRepository;

    @Inject
    TarjetaRepository tarjetaRepository;

    @Inject
    EmpleadoRepository empleadoRepository;

    @Override
    @Transactional
    public RecursoAsignadoDTO crearRecursoAsignado(RecursoAsignadoDTO dto) throws IllegalArgumentException {
        
        System.out.println("Creando recurso asignado...");
        
        // Validaciones
        if (dto.getTarjetaId() == null) {
            throw new IllegalArgumentException("El ID de la tarjeta es requerido");
        }

        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El ID del empleado es requerido");
        }

        // Verificar que la tarjeta existe
        tarjeta tarjeta = tarjetaRepository.findById(dto.getTarjetaId());
        if (tarjeta == null) {
            throw new IllegalArgumentException("Tarjeta no encontrada con ID: " + dto.getTarjetaId());
        }

        // Verificar que el empleado existe
        empleado empleado = empleadoRepository.findById(dto.getEmpleadoId());
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + dto.getEmpleadoId());
        }

        // Verificar que no exista ya una asignación activa de esta tarjeta al empleado
        List<recursoAsignado> asignacionesExistentes = recursoAsignadoRepository
            .find("tarjeta.tarjetaId = ?1 and empleado.empleadoId = ?2 and estado = ?3", 
                  dto.getTarjetaId(), dto.getEmpleadoId(), "Activo")
            .list();

        if (!asignacionesExistentes.isEmpty()) {
            throw new IllegalArgumentException("Esta tarjeta ya está asignada activamente a este empleado");
        }

        System.out.println("Asignando tarjeta " + tarjeta.getNumeroTarjeta() + " al empleado " + empleado.getNombres());

        // Parsear fecha de asignación
        Date fechaAsignacion = new Date(); // Fecha actual por defecto
        if (dto.getFechaAsignacion() != null && !dto.getFechaAsignacion().trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                fechaAsignacion = sdf.parse(dto.getFechaAsignacion());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Formato de fecha incorrecto. Use yyyy-MM-dd");
            }
        }

        // Crear nuevo recurso asignado
        recursoAsignado nuevoRecurso = new recursoAsignado();
        nuevoRecurso.setTarjeta(tarjeta);
        nuevoRecurso.setEmpleado(empleado);
        nuevoRecurso.setFechaAsignacion(fechaAsignacion);
        nuevoRecurso.setMontoMaximo(dto.getMontoMaximo());
        nuevoRecurso.setEstado(dto.getEstado() != null ? dto.getEstado() : "Activo");

        try {
            recursoAsignadoRepository.persist(nuevoRecurso);
            recursoAsignadoRepository.flush();
            System.out.println("Recurso asignado creado con ID: " + nuevoRecurso.getRecursoId());
        } catch (Exception e) {
            System.err.println("Error al persistir recurso: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al guardar el recurso asignado en la base de datos", e);
        }

        // Retornar DTO
        return mapToDTO(nuevoRecurso);
    }

    @Override
    public List<RecursoAsignadoDTO> obtenerRecursosPorEmpleado(Long empleadoId) throws IllegalArgumentException {
        
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
        }

        // Verificar que el empleado existe
        empleado empleado = empleadoRepository.findById(empleadoId);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + empleadoId);
        }

        System.out.println("Buscando recursos para empleado: " + empleado.getNombres() + " " + empleado.getApellidos());

        // Obtener recursos del empleado
        List<recursoAsignado> recursos = recursoAsignadoRepository
            .find("empleado.empleadoId", empleadoId)
            .list();

        System.out.println("Recursos encontrados: " + recursos.size());

        // Mapear a DTO
        return recursos.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public RecursoAsignadoDTO obtenerRecursoPorId(Long recursoId) throws IllegalArgumentException {
        
        if (recursoId == null) {
            throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
        }

        recursoAsignado recurso = recursoAsignadoRepository.findById(recursoId);
        if (recurso == null) {
            throw new IllegalArgumentException("Recurso no encontrado con ID: " + recursoId);
        }

        return mapToDTO(recurso);
    }

    @Override
    public List<RecursoAsignadoDTO> listarTodosLosRecursos() {
        
        System.out.println("Listando todos los recursos asignados");
        
        List<recursoAsignado> recursos = recursoAsignadoRepository.listAll();
        
        System.out.println("Total de recursos: " + recursos.size());
        
        return recursos.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void desactivarRecurso(Long recursoId) throws IllegalArgumentException {
        
        if (recursoId == null) {
            throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
        }

        recursoAsignado recurso = recursoAsignadoRepository.findById(recursoId);
        if (recurso == null) {
            throw new IllegalArgumentException("Recurso no encontrado con ID: " + recursoId);
        }

        System.out.println("Desactivando recurso ID: " + recursoId);

        recurso.setEstado("Inactivo");
        
        try {
            recursoAsignadoRepository.persist(recurso);
            System.out.println("Recurso desactivado");
        } catch (Exception e) {
            System.err.println("Error al desactivar recurso: " + e.getMessage());
            throw new RuntimeException("Error al desactivar el recurso", e);
        }
    }

    @Override
    @Transactional
    public RecursoAsignadoDTO actualizarRecursoAsignado(Long recursoId, Double montoMaximo, String estado) throws IllegalArgumentException {
        
        System.out.println("Actualizando recurso asignado ID: " + recursoId);
        
        if (recursoId == null) {
            throw new IllegalArgumentException("El ID del recurso no puede ser nulo");
        }

        // Buscar el recurso asignado
        recursoAsignado recurso = recursoAsignadoRepository.findById(recursoId);
        if (recurso == null) {
            throw new IllegalArgumentException("Recurso no encontrado con ID: " + recursoId);
        }

        // Validar estado si se proporciona
        if (estado != null && !estado.trim().isEmpty()) {
            if (!estado.equals("Activo") && !estado.equals("Inactivo")) {
                throw new IllegalArgumentException("El estado debe ser 'Activo' o 'Inactivo'");
            }
            recurso.setEstado(estado);
            System.out.println("Estado actualizado a: " + estado);
        }

        // Actualizar monto máximo si se proporciona
        if (montoMaximo != null) {
            if (montoMaximo < 0) {
                throw new IllegalArgumentException("El monto máximo no puede ser negativo");
            }
            recurso.setMontoMaximo(montoMaximo);
            System.out.println("Monto máximo actualizado a: " + montoMaximo);
        }

        try {
            recursoAsignadoRepository.persist(recurso);
            recursoAsignadoRepository.flush();
            System.out.println("Recurso asignado actualizado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al actualizar recurso: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el recurso asignado", e);
        }

        return mapToDTO(recurso);
    }

    @Override
    public RecursoAsignadoDTO obtenerRecursoPorTarjetaYEmpleado(Long tarjetaId, Long empleadoId) throws IllegalArgumentException {
        
        System.out.println("Buscando recurso para tarjeta: " + tarjetaId + " y empleado: " + empleadoId);
        
        if (tarjetaId == null) {
            throw new IllegalArgumentException("El ID de la tarjeta no puede ser nulo");
        }
        
        if (empleadoId == null) {
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo");
        }

        // Buscar recurso asignado para esta tarjeta y empleado
        List<recursoAsignado> recursos = recursoAsignadoRepository
            .find("tarjeta.tarjetaId = ?1 and empleado.empleadoId = ?2", tarjetaId, empleadoId)
            .list();

        if (recursos.isEmpty()) {
            throw new IllegalArgumentException("No se encontró asignación de tarjeta para este empleado");
        }

        // Si hay múltiples asignaciones, priorizar la activa, sino la más reciente
        recursoAsignado recurso = recursos.stream()
            .filter(r -> "Activo".equals(r.getEstado()))
            .findFirst()
            .orElse(recursos.get(0));

        System.out.println("Recurso encontrado con ID: " + recurso.getRecursoId());

        return mapToDTO(recurso);
    }

    /**
     * Mapea una entidad recursoAsignado a DTO
     */
    private RecursoAsignadoDTO mapToDTO(recursoAsignado recurso) {
        return new RecursoAsignadoDTO(
            recurso.getRecursoId(),
            recurso.getEmpleado() != null ? recurso.getEmpleado().getEmpleadoId() : null,
            recurso.getTarjeta() != null ? recurso.getTarjeta().getTarjetaId() : null,
            recurso.getFechaAsignacion() != null ? 
                new SimpleDateFormat("yyyy-MM-dd").format(recurso.getFechaAsignacion()) : null,
            recurso.getMontoMaximo(),
            recurso.getEstado()
        );
    
    }

    /////////////////////////////////////////////////////////
    /// Informacion de Recursos por empleado
    /////////////////////////////////////////////////////////

     @Inject
EntityManager em;

public InformacionRecursoDTO obtenerInformacionPorEmpleadoYTarjeta(Long empleadoId, Long tarjetaId)
    throws IllegalArgumentException {

    Object[] result = (Object[]) em.createNativeQuery("""
        SELECT 
            e.empleadoid,
            e.nombres || ' ' || e.apellidos AS nombreEmpleado,
            t.numerotarjeta,
            TO_CHAR(r.fechaasignacion, 'YYYY-MM-DD') AS fechaAsignacion,
            r.estado,
            r.montomaximo,
            NVL(SUM(g.totalmonedabase), 0) AS totalGastado,
            ROUND((NVL(SUM(g.totalmonedabase), 0) / r.montomaximo) * 100, 2) AS porcentaje,
            (r.montomaximo - NVL(SUM(g.totalmonedabase), 0)) AS resto
        FROM recursoasignado r
        JOIN empleado e ON r.empleadoid = e.empleadoid
        JOIN tarjeta t ON t.tarjetaid = r.tarjetaid
        LEFT JOIN gasto g ON g.recursoid = r.recursoid
        WHERE r.empleadoid = :empleadoId AND r.tarjetaid = :tarjetaId
        GROUP BY 
            e.empleadoid, e.nombres, e.apellidos, 
            t.numerotarjeta, r.fechaasignacion, r.estado, r.montomaximo
    """)
    .setParameter("empleadoId", empleadoId)
    .setParameter("tarjetaId", tarjetaId)
    .getSingleResult();
    
    Long id = ((Number) result[0]).longValue();
    String nombre = (String) result[1];
    String tarjeta = (String) result[2];
    String fecha = (String) result[3];
    String estado = (String) result[4];
    Double montoMaximo = ((Number) result[5]).doubleValue();
    Double totalGastado = ((Number) result[6]).doubleValue();
    Double porcentaje = ((Number) result[7]).doubleValue();
    Double resto = ((Number) result[8]).doubleValue();
    Double montoActual = resto;
    
    return new InformacionRecursoDTO(
        id,
        nombre,
        tarjeta,
        fecha,
        estado,
        montoMaximo,
        montoActual,
        porcentaje,
        resto
    );
}
}