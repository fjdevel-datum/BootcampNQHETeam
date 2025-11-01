package com.easycheck.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


public record DetalleGastoDTO(
    Long empleadoId,
    String nombreCompleto,
    String rol,
    String nombreCentro,
    String empresaNombre,
    String nombreActividad,
    String descripcionGasto,
    BigDecimal totalGasto,
    String simboloMoneda,
    LocalDate fechaGasto,
    Long recursoId,
    Long tarjetaId,
    String numeroTarjeta
) {}