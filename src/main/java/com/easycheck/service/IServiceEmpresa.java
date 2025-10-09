package com.easycheck.service;

import com.easycheck.dto.empresaDTO;

public interface IServiceEmpresa {
    
    empresaDTO crearEmpresa(empresaDTO dto) throws IllegalArgumentException;
}
