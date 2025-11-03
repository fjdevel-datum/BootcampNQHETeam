package com.easycheck.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetalleGastoTarjetaDTO(
    Long empleadoId,
    String nombreCompleto,
    String rol,
    String nombreCentro,
    String empresaNombre,
    String numeroTarjeta,
    String tipoTarjeta,
    String nombreActividad,
    String descripcionGasto,
    BigDecimal totalGasto,
    String simboloMoneda,
    LocalDate fechaGasto
) {}