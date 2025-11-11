package com.easycheck.domain.service;

import com.easycheck.application.dto.empresaDTO;

public interface IServiceEmpresa {
    
    empresaDTO crearEmpresa(empresaDTO dto) throws IllegalArgumentException;
}
