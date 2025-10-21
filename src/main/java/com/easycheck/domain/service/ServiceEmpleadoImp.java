package com.easycheck.domain.service;

import com.easycheck.application.dto.EmpleadoDTO;
import com.easycheck.domain.model.centroCosto;
import com.easycheck.domain.model.empresa;
import com.easycheck.domain.model.empleado;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.centroCostoRepository;
import com.easycheck.infrastructure.repository.empresaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceEmpleadoImp implements IServiceEmpleado {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    empresaRepository empresaRepository;

    @Inject
    centroCostoRepository centroCostoRepository;

    @Override
    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) throws IllegalArgumentException {
        // Validar datos básicos
        if (dto.getNombres() == null || dto.getNombres().trim().isEmpty()) {
            throw new IllegalArgumentException("Los nombres son requeridos");
        }

        if (dto.getApellidos() == null || dto.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son requeridos");
        }

        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("El ID de empresa es requerido");
        }

        if (dto.getCentroId() == null) {
            throw new IllegalArgumentException("El ID de centro de costo es requerido");
        }

        // Validar llaves foráneas
        empresa empresa = empresaRepository.findById(dto.getEmpresaId());
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa no encontrada con ID: " + dto.getEmpresaId());
        }

        centroCosto centroCosto = centroCostoRepository.findById(dto.getCentroId());
        if (centroCosto == null) {
            throw new IllegalArgumentException("Centro de costo no encontrado con ID: " + dto.getCentroId());
        }

        // Verificar si ya existe un empleado con el mismo documento
        if (dto.getDocumentoIdentidad() != null && !dto.getDocumentoIdentidad().trim().isEmpty()) {
            empleado existente = empleadoRepository
                .find("documentoIdentidad", dto.getDocumentoIdentidad())
                .firstResult();
            if (existente != null) {
                throw new IllegalArgumentException("Ya existe un empleado con el documento: " + dto.getDocumentoIdentidad());
            }
        }

        // Verificar si ya existe un empleado con el mismo UID de Firebase
        if (dto.getUid() != null && !dto.getUid().trim().isEmpty()) {
            empleado existenteUid = empleadoRepository
                .find("firebase_uid", dto.getUid())
                .firstResult();
            if (existenteUid != null) {
                throw new IllegalArgumentException("Ya existe un empleado con este UID de Firebase");
            }
        }

        // Crear el nuevo empleado
        empleado nuevoEmpleado = new empleado();
        nuevoEmpleado.setNombres(dto.getNombres());
        nuevoEmpleado.setApellidos(dto.getApellidos());
        nuevoEmpleado.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoEmpleado.setEmpresa(empresa);
        nuevoEmpleado.setCentroCosto(centroCosto);
        nuevoEmpleado.setUid(dto.getUid());
        nuevoEmpleado.setRol(dto.getRol() != null ? dto.getRol() : com.easycheck.domain.model.rol.EMPLEADO);

        try {
            empleadoRepository.persist(nuevoEmpleado);
            System.out.println("Empleado creado con ID: " + nuevoEmpleado.getEmpleadoId());
        } catch (Exception e) {
            System.err.println("Error al crear empleado: " + e.getMessage());
            throw new RuntimeException("Error al guardar el empleado en la base de datos", e);
        }

        // Retornar el DTO de respuesta
        return mapToDTO(nuevoEmpleado);
    }

    @Override
    @Transactional
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) throws IllegalArgumentException {
        // Buscar el empleado existente
        empleado empleado = empleadoRepository.findById(id);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }

        // Validar y actualizar empresa si se proporciona
        if (dto.getEmpresaId() != null) {
            empresa empresa = empresaRepository.findById(dto.getEmpresaId());
            if (empresa == null) {
                throw new IllegalArgumentException("Empresa no encontrada con ID: " + dto.getEmpresaId());
            }
            empleado.setEmpresa(empresa);
        }

        // Validar y actualizar centro de costo si se proporciona
        if (dto.getCentroId() != null) {
            centroCosto centroCosto = centroCostoRepository.findById(dto.getCentroId());
            if (centroCosto == null) {
                throw new IllegalArgumentException("Centro de costo no encontrado con ID: " + dto.getCentroId());
            }
            empleado.setCentroCosto(centroCosto);
        }

        // Actualizar campos básicos
        if (dto.getNombres() != null && !dto.getNombres().trim().isEmpty()) {
            empleado.setNombres(dto.getNombres());
        }

        if (dto.getApellidos() != null && !dto.getApellidos().trim().isEmpty()) {
            empleado.setApellidos(dto.getApellidos());
        }

        if (dto.getDocumentoIdentidad() != null) {
            // Verificar que no exista otro empleado con el mismo documento
            empleado existente = empleadoRepository
                .find("documentoIdentidad", dto.getDocumentoIdentidad())
                .firstResult();
            if (existente != null && !existente.getEmpleadoId().equals(id)) {
                throw new IllegalArgumentException("Ya existe otro empleado con el documento: " + dto.getDocumentoIdentidad());
            }
            empleado.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        }

        if (dto.getRol() != null) {
            empleado.setRol(dto.getRol());
        }

        try {
            empleadoRepository.persist(empleado);
            System.out.println("Empleado actualizado con ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            throw new RuntimeException("Error al actualizar el empleado", e);
        }

        return mapToDTO(empleado);
    }

    @Override
    public EmpleadoDTO obtenerEmpleadoPorId(Long id) throws IllegalArgumentException {
        empleado empleado = empleadoRepository.findById(id);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }
        return mapToDTO(empleado);
    }

    @Override
    public List<EmpleadoDTO> listarTodosLosEmpleados() {
        List<empleado> empleados = empleadoRepository.listAll();
        return empleados.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<EmpleadoDTO> listarEmpleadosPorEmpresa(Long empresaId) {
        empresa empresa = empresaRepository.findById(empresaId);
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa no encontrada con ID: " + empresaId);
        }

        System.out.println("Buscando empleados de la empresa: " + empresa.getEmpresaNombre());

    // Obtener empleados y filtrar los que NO sean ADMIN
        List<empleado> empleados = empleadoRepository.find("empresa", empresa).list();
    
        List<EmpleadoDTO> resultado = empleados.stream()
        .filter(emp -> emp.getRol() != com.easycheck.domain.model.rol.ADMIN) // Filtrar ADMIN
        .map(this::mapToDTO)
        .collect(Collectors.toList());


        return resultado;
    }

    @Override
    public List<EmpleadoDTO> listarEmpleadosPorCentroCosto(Long centroId) {
        centroCosto centro = centroCostoRepository.findById(centroId);
        if (centro == null) {
            throw new IllegalArgumentException("Centro de costo no encontrado con ID: " + centroId);
        }

        List<empleado> empleados = empleadoRepository.find("centroCosto", centro).list();
        return empleados.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public empleado buscarPorUid(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("El UID no puede estar vacío");
        }
        return empleadoRepository.find("firebase_uid", uid).firstResult();
    }

    @Override
    public EmpleadoDTO buscarPorUidDTO(String uid) {
        empleado empleado = buscarPorUid(uid);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con UID: " + uid);
        }
        return mapToDTO(empleado);
    }

    @Override
    public EmpleadoDTO buscarPorDocumento(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            throw new IllegalArgumentException("El documento de identidad no puede estar vacío");
        }

        empleado empleado = empleadoRepository
            .find("documentoIdentidad", documentoIdentidad)
            .firstResult();

        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con documento: " + documentoIdentidad);
        }

        return mapToDTO(empleado);
    }

    @Override
    @Transactional
    public void eliminarEmpleado(Long id) throws IllegalArgumentException {
        empleado empleado = empleadoRepository.findById(id);
        if (empleado == null) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + id);
        }

        try {
            empleadoRepository.delete(empleado);
            System.out.println("Empleado eliminado con ID: " + id);
        } catch (Exception e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            throw new RuntimeException("Error al eliminar el empleado. Puede tener registros relacionados.", e);
        }
    }

    @Override
    public long contarEmpleadosPorEmpresa(Long empresaId) {
        empresa empresa = empresaRepository.findById(empresaId);
        if (empresa == null) {
            throw new IllegalArgumentException("Empresa no encontrada con ID: " + empresaId);
        }

        return empleadoRepository.count("empresa", empresa);
    }

    @Override
    public boolean existeEmpleadoConDocumento(String documentoIdentidad) {
        if (documentoIdentidad == null || documentoIdentidad.trim().isEmpty()) {
            return false;
        }

        empleado empleado = empleadoRepository
            .find("documentoIdentidad", documentoIdentidad)
            .firstResult();

        return empleado != null;
    }

    @Override
    public boolean existeEmpleadoConUid(String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            return false;
        }

        empleado empleado = empleadoRepository
            .find("firebase_uid", uid)
            .firstResult();

        return empleado != null;
    }

    /**
     * Mapea una entidad empleado a DTO
     */
    private EmpleadoDTO mapToDTO(empleado empleado) {
        return new EmpleadoDTO(
            empleado.getEmpleadoId(),
            empleado.getNombres(),
            empleado.getApellidos(),
            empleado.getDocumentoIdentidad(),
            empleado.getEmpresa() != null ? empleado.getEmpresa().getEmpresaId() : null,
            empleado.getCentroCosto() != null ? empleado.getCentroCosto().getCentroId() : null,
            empleado.getUid(),
            empleado.getRol()
        );
    }
}