package com.easycheck.service;

import com.easycheck.dto.EmpleadoDTO;
import com.easycheck.model.centroCosto;
import com.easycheck.model.empleado;
import com.easycheck.model.empresa;
import com.easycheck.repository.EmpleadoRepository;
import com.easycheck.repository.centroCostoRepository;
import com.easycheck.repository.empresaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) throws IllegalArgumentException
    {
        //validamos las llaves foraneas

        empresa empresa = empresaRepository.findById(dto.getEmpresaId());
        if(empresa==null)
        {
            throw new IllegalArgumentException("Empresa no encontrada");
        }
        
        centroCosto centroCosto = centroCostoRepository.findById(dto.getCentroId());
        if(centroCosto == null)
        {
            throw new IllegalArgumentException("Centro de costo no encontrado");
        }

        

        empleado nuevoEmpleado = new empleado();

        nuevoEmpleado.setNombres(dto.getNombres());
        nuevoEmpleado.setApellidos(dto.getApellidos());
        nuevoEmpleado.setDocumentoIdentidad(dto.getDocumentoIdentidad());
        nuevoEmpleado.setEmpresa(empresa);
        nuevoEmpleado.setCentroCosto(centroCosto);

        empleadoRepository.persist(nuevoEmpleado);

        EmpleadoDTO respuesta = new EmpleadoDTO(
            nuevoEmpleado.getEmpleadoId(),
            nuevoEmpleado.getNombres(),
            nuevoEmpleado.getApellidos(),
            nuevoEmpleado.getDocumentoIdentidad(),
            empresa.getEmpresaId(),
            centroCosto.getCentroId()
        );
        
        return respuesta;


    




    }



}
