package com.easycheck.domain.service;

import com.easycheck.domain.model.empleado;
import com.easycheck.domain.model.moneda;
import com.easycheck.infrastructure.repository.EmpleadoRepository;
import com.easycheck.infrastructure.repository.monedaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class MonedaService implements IServiceMoneda {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    monedaRepository monedaRepository;

    // Tabla simple de tasas de cambio base USD
    private static final Map<String, Double> tasasCambio = new HashMap<>();

    static {
        tasasCambio.put("USD", 1.00);
        tasasCambio.put("GTQ", 0.13); // 1 Quetzal = 0.13 USD aprox
        tasasCambio.put("PAB", 1.00); // Balboa == Dólar
    }
    @Override
    public Double convertirAMonedaBase(Long empleadoId, String monedaGastoCodigo, Double monto) {
        empleado emp = empleadoRepository.findById(empleadoId);
        if (emp == null || emp.getEmpresa() == null || emp.getEmpresa().getMonedaBase() == null) {
            return null;
        }

        moneda monedaBase = emp.getEmpresa().getMonedaBase();
        String codigoBase = monedaBase.getCodigoISO();

        Double tasaGasto = tasasCambio.get(monedaGastoCodigo.toUpperCase());
        Double tasaBase = tasasCambio.get(codigoBase.toUpperCase());

        if (tasaGasto == null || tasaBase == null) {
            return null;
        }

        // Convertir primero a USD y luego a moneda base
        double montoEnUSD = monto * tasaGasto;
        double montoEnBase = montoEnUSD / tasaBase;

        return Math.round(montoEnBase * 100.0) / 100.0; // redondear a 2 decimales
    }
}
