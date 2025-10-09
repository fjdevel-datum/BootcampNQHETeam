package com.easycheck.service;

import com.easycheck.dto.empresaDTO;
import com.easycheck.model.empresa;
import com.easycheck.model.moneda;
import com.easycheck.model.pais;
import com.easycheck.repository.PaisRepository;
import com.easycheck.repository.empresaRepository;
import com.easycheck.repository.monedaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ServiceEmpresaImp implements IServiceEmpresa {
    
    @Inject 
    empresaRepository empresaRepository;

    @Inject
    PaisRepository paisRepository;

    @Inject
    monedaRepository monedaRepository;

    @Override
    @Transactional
    public empresaDTO crearEmpresa(empresaDTO dto) throws IllegalArgumentException{


        //validamos pais
        pais pais = paisRepository.findById(dto.getPaisId());
        if(pais==null)
        {
            throw new IllegalArgumentException("Pais no encontrado");
        }
        moneda moneda = monedaRepository.findById(dto.getMonedaId());
        if(moneda==null)
        {
            throw new IllegalArgumentException("Moneda no encontrado");
        }

        //creamos y persistimos
        empresa nueva = new empresa();
        nueva.setEmpresaNombre(dto.getEmpresaNombre());
        nueva.setUbicacion(dto.getUbicacion());
        nueva.setPais(pais);
        nueva.setMonedaBase(moneda);

        empresaRepository.persist(nueva);

        empresaDTO respuesta = new empresaDTO(
            nueva.getEmpresaId(),
            nueva.getEmpresaNombre(),
            nueva.getUbicacion(),
            pais.getPaisId(),
            moneda.getMonedaId()
        );
        return respuesta;


    }




}
