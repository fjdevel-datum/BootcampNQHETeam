package com.easycheck.domain.service;

import com.easycheck.application.dto.FacturaDTO;
import com.easycheck.domain.model.factura;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.oracle.svm.core.annotate.Inject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;



@ApplicationScoped
public class ServiceFacturaImp implements IServiceFactura{
    
    @Inject
    FacturaRepository facturaRepository;

    @Override
    @Transactional
    public FacturaDTO crearFactura(FacturaDTO dto) throws IllegalArgumentException
    {

        factura nuevFactura = new factura();
        nuevFactura.setComprobante(dto.getComprobante());
        facturaRepository.persist(nuevFactura);

        FacturaDTO factura = new FacturaDTO(
            nuevFactura.getFacturaId(),
            nuevFactura.getComprobante()
        );
        
        return factura;


    }

}
