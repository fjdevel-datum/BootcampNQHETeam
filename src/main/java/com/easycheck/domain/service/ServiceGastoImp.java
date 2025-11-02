package com.easycheck.domain.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.DetalleGastoDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.factura;
import com.easycheck.domain.model.gasto;
import com.easycheck.domain.model.moneda;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tipoGasto;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.easycheck.infrastructure.repository.GastoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.monedaRepository;
import com.easycheck.infrastructure.repository.tipoGastoRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;





import java.sql.*;






@ApplicationScoped 
public class ServiceGastoImp implements IServiceGasto {
    
    @Inject
    RecursoAsignadoRepository recursoAsignadoRepository;

    @Inject
    ActividadRepository actividadRepository;

    @Inject
    tipoGastoRepository tipoGastoRepository;

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    monedaRepository monedaRepository;

    @Inject
    GastoRepository gastoRepository;

@Override
@Transactional
public GastoDTO crearGasto(GastoDTO gasto) throws IllegalArgumentException
{
    // Validar moneda
    moneda moneda = monedaRepository.findById(gasto.getMonedaGasto());
    if(moneda == null)
    {
        throw new IllegalArgumentException("Moneda no encontrada");
    }
    
    // ✅ FACTURA ES OPCIONAL
    factura factura = null;
    if(gasto.getFacturaId() != null) {
        factura = facturaRepository.findById(gasto.getFacturaId());
        if(factura == null)
        {
            throw new IllegalArgumentException("Factura no encontrada con ID: " + gasto.getFacturaId());
        }
    }
    
    // Validar recurso
    recursoAsignado recurso = recursoAsignadoRepository.findById(gasto.getRecursoId());
    if(recurso == null)
    {
        throw new IllegalArgumentException("Recurso no encontrado");
    }
    
    // Validar tipo de gasto
    tipoGasto tipoGasto = tipoGastoRepository.findById(gasto.getTipoGastoId());
    if(tipoGasto == null)
    {
        throw new IllegalArgumentException("Tipo de gasto no encontrado");
    }
    
    // Validar actividad
    actividad actividad = actividadRepository.findById(gasto.getActividadId());
    if(actividad == null)
    {
        throw new IllegalArgumentException("Actividad no encontrada");
    }

    Date fechagasto;
    try {
        LocalDate localDate = LocalDate.parse(gasto.getFecha(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fechagasto = Date.valueOf(localDate);
    } catch (Exception e) {
        throw new IllegalArgumentException("Formato de fecha incorrecto. Use el formato yyyy-MM-dd.");
    }

    gasto nuevoGasto = new gasto();
    nuevoGasto.setFecha(fechagasto);
    nuevoGasto.setMonedaGasto(moneda);
    nuevoGasto.setFactura(factura); // Puede ser null
    nuevoGasto.setRecursoAsignado(recurso);
    nuevoGasto.setTipoGasto(tipoGasto);
    nuevoGasto.setActividad(actividad);
    nuevoGasto.setDescripcionGasto(gasto.getDescripcionGasto());
    nuevoGasto.setTotalGasto(gasto.getTotalGasto());
    nuevoGasto.setTotalMonedaBase(gasto.getTotalMonedaBase());

    gastoRepository.persist(nuevoGasto);

    GastoDTO gastoDTO = new GastoDTO(
        nuevoGasto.getGastoId(),
        gasto.getFecha(),
        moneda.getMonedaId(),
        factura != null ? factura.getFacturaId() : null,
        recurso.getRecursoId(),
        tipoGasto.getTipoGastoId(),
        actividad.getActividadId(),
        nuevoGasto.getDescripcionGasto(),
        nuevoGasto.getTotalGasto(),
        nuevoGasto.getTotalMonedaBase()
    );
    return gastoDTO;
}

    @Override
public List<GastoDTO> obtenerGastosPorActividad(Long actividadId) {
    List<gasto> gastos = gastoRepository.findByActividadId(actividadId);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    return gastos.stream()
        .map(g -> new GastoDTO(
            g.getGastoId(),
            g.getFecha() != null ? sdf.format(g.getFecha()) : null,
            g.getMonedaGasto() != null ? g.getMonedaGasto().getMonedaId() : null,
            g.getFactura() != null ? g.getFactura().getFacturaId() : null,
            g.getRecursoAsignado() != null ? g.getRecursoAsignado().getRecursoId() : null,
            g.getTipoGasto() != null ? g.getTipoGasto().getTipoGastoId() : null,
            g.getActividad() != null ? g.getActividad().getActividadId() : null,
            g.getDescripcionGasto(),
            g.getTotalGasto(),
            g.getTotalMonedaBase()
        ))
        .toList();
}


    @Inject
    EntityManager em;

    public byte[] generarReporteExcel(Long empleadoId)
    {
        Object[] result = (Object[]) em.createNativeQuery("""
            SELECT 
                EMPLEADOID,
                NOMBRE_COMPLETO,
                ROL,
                NOMBRECENTRO,
                EMPRESANOMBRE,
                TOTAL_ACTIVIDADES,
                TOTAL_GASTOS,
                SUMA_TOTAL_GASTOS,
                PROMEDIO_GASTO,
                GASTO_MINIMO,
                GASTO_MAXIMO,
                SUMA_MONEDA_BASE,
                PRIMER_GASTO,
                ULTIMO_GASTO,
                SIMBOLO_MONEDA
            FROM V_GASTOS_POR_EMPLEADO
            WHERE EMPLEADOID = :empleadoId
            AND PRIMER_GASTO >= TO_DATE(:fechaInicio, 'YYYY-MM-DD')
            AND ULTIMO_GASTO < TO_DATE(:fechaFin, 'YYYY-MM-DD')
            """)
            .setParameter("empleadoId", empleadoId)
            .getSingleResult();

            try(Workbook workbook = new XSSFWorkbook()) {
                
                Sheet sheet = workbook.createSheet("Resumen Gastos");

                //Headers
                Row header = sheet.createRow(0);

                String[] headers = {
                     "Empleado ID", "Nombre Completo", "Rol", "Centro de Costo", "Empresa",
                    "Total Actividades", "Total Gastos", "Suma Total", "Promedio", "Mínimo", "Máximo",
                    "Suma Moneda Base", "Primer Gasto", "Último Gasto", "Símbolo Moneda"

                };

                for(int i=0;i<headers.length;i++)
                {
                    header.createCell(i).setCellValue(headers[i]);

                }
                //Datos
                Row row = sheet.createRow(1);
                for (int i = 0; i < result.length; i++) {
                    Object val = result[i];
                    if (val instanceof java.sql.Date date) {
                        row.createCell(i).setCellValue(date.toString());
                    } else if (val instanceof Number num) {
                        row.createCell(i).setCellValue(num.doubleValue());
                    } else {
                        row.createCell(i).setCellValue(val != null ? val.toString() : "N/A");
                    }
                }
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                return out.toByteArray();





            } catch (IOException  e) {
                throw new RuntimeException("Error generando el reporte de gastos", e);

            }
    }


    ///////////////////////////////////////////////////////////////////////////////

    @Inject
    DataSource dataSource; // Inyectamos el pool de conexiones de Quarkus

    @Override
    public List<DetalleGastoDTO> getDetalleGastos(Long empleadoId, LocalDate fechaInicio, LocalDate fechaFinal) {
        
        List<DetalleGastoDTO> gastos = new ArrayList<>();
        
        // Esta es la forma de llamar a una función de tabla en Oracle
        String sql = "SELECT * FROM TABLE(C##EASYCHECK.F_DETALLE_GASTOS_EMPLEADO(?, ?, ?))";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Asignamos los parámetros
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFinal));
            ps.setLong(3, empleadoId);

            try (ResultSet rs = ps.executeQuery()) {
                // Iteramos sobre los resultados y los mapeamos al DTO
                while (rs.next()) {
                    DetalleGastoDTO gasto = new DetalleGastoDTO(
                        rs.getLong("EmpleadoId"),
                        rs.getString("Nombre_Completo"),
                        rs.getString("Rol"),
                        rs.getString("NombreCentro"),
                        rs.getString("EmpresaNombre"),
                        rs.getString("NombreActividad"),
                        rs.getString("DescripcionGasto"),
                        rs.getBigDecimal("TotalGasto"),
                        rs.getString("SimboloMoneda"),
                        rs.getDate("FechaGasto").toLocalDate(),
                        rs.getLong("RecursoId"),
                        rs.getLong("TarjetaId"),
                        rs.getString("NumeroTarjeta")
                    );
                    gastos.add(gasto);
                }
            }
        } catch (SQLException e) {
            // En una app real, aquí deberías manejar mejor la excepción
            throw new RuntimeException("Error al consultar la base de datos", e);
        }
        
        return gastos;
    }
    
}
