package com.easycheck.domain.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.easycheck.application.dto.GastoDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.factura;
import com.easycheck.domain.model.gasto;
import com.easycheck.domain.model.moneda;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tarjeta;
import com.easycheck.domain.model.tipoGasto;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.easycheck.infrastructure.repository.GastoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.TarjetaRepository;
import com.easycheck.infrastructure.repository.monedaRepository;
import com.easycheck.infrastructure.repository.tipoGastoRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;

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
    public GastoDTO crearGasto(GastoDTO gasto)throws IllegalArgumentException
    {
        //validamos
        moneda moneda = monedaRepository.findById(gasto.getMonedaGasto());
        if(moneda==null)
        {
            throw new IllegalArgumentException("Moneda no encontrada");
        }
        factura factura = facturaRepository.findById(gasto.getFacturaId());
        if(factura==null)
        {
            throw new IllegalArgumentException("Factura no encontrada");
        }
        recursoAsignado recurso = recursoAsignadoRepository.findById(gasto.getRecursoId());
        if(recurso==null)
        {
            throw new IllegalArgumentException("Recurso no encontrado");
        }
        tipoGasto tipoGasto = tipoGastoRepository.findById(gasto.getTipoGastoId());
        if(tipoGasto==null)
        {
            throw new IllegalArgumentException("Tipo de gasto no encontrado");
        }
        actividad actividad = actividadRepository.findById(gasto.getActividadId());
        if(actividad==null)
        {
            throw new IllegalArgumentException("Actividad no encontrada");
        }

        Date fechagasto;
        try {
            LocalDate localDate = LocalDate.parse(gasto.getFecha(),DateTimeFormatter.ofPattern("yyy-MM-dd"));
            fechagasto = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha de expiración incorrecto. Use el formato yyyy-MM-dd.");
        }

        gasto nuevoGasto = new gasto();
        nuevoGasto.setFecha(fechagasto);
        nuevoGasto.setMonedaGasto(moneda);
        nuevoGasto.setFactura(factura);
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
            factura.getFacturaId(),
            recurso.getRecursoId(),
            tipoGasto.getTipoGastoId(),
            actividad.getActividadId(),
            nuevoGasto.getDescripcionGasto(),
            nuevoGasto.getTotalGasto(),
            nuevoGasto.getTotalMonedaBase()
        );
        return gastoDTO;




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


    /*@Inject
    EntityManager entityManager;

    @Inject
    TarjetaRepository tarjetaRepository;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Transactional
    public gasto saveGasto(GastoDTO dto) throws IllegalArgumentException {
        validateDTO(dto);

        Date fecha = parseDate(dto.getFecha());

        // Buscar tarjeta y recurso asignado
        tarjeta foundTarjeta = findTarjeta(dto.getNumero_Tarjeta());
        recursoAsignado recurso = findRecursoAsignado(foundTarjeta);

        // Construir y persistir el gasto
        gasto g = new gasto();
        g.setFecha(fecha);
        g.setDescripcionGasto(dto.getDescripcion_Item() != null ? 
            dto.getDescripcion_Item() : dto.getNombre_Pagador());
        g.setTotalGasto(dto.getMonto_Total());
        
        // Establecer relaciones si existen los IDs
        if (dto.getMonedaGastoId() != null) {
            moneda monedaGasto = em.find(moneda.class, dto.getMonedaGastoId());
            if (monedaGasto != null) {
                g.setMonedaGasto(monedaGasto);
            }
        }

        if (dto.getFacturaId() != null) {
            factura factura = em.find(factura.class, dto.getFacturaId());
            if (factura != null) {
                g.setFactura(factura);
            }
        }

        if (dto.getTipoGastoId() != null) {
            tipoGasto tipoGasto = em.find(tipoGasto.class, dto.getTipoGastoId());
            if (tipoGasto != null) {
                g.setTipoGasto(tipoGasto);
            }
        }

        if (dto.getActividadId() != null) {
            actividad actividad = em.find(actividad.class, dto.getActividadId());
            if (actividad != null) {
                g.setActividad(actividad);
            }
        }

        // Establecer total en moneda base
        g.setTotalMonedaBase(dto.getTotalMonedaBase() != null ? 
            dto.getTotalMonedaBase() : dto.getMonto_Total());

        if (recurso != null) {
            g.setRecursoAsignado(recurso);
        }

        em.persist(g);
        em.flush();

        return g;
    }

    private void validateDTO(GastoDTO dto) {
        if (dto == null) throw new IllegalArgumentException("payload vacío");
        if (dto.getFecha() == null || dto.getFecha().trim().isEmpty())
            throw new IllegalArgumentException("Fecha requerida (YYYY-MM-DD)");
        if (dto.getMonto_Total() == null)
            throw new IllegalArgumentException("Monto_Total requerido");
    }

    private Date parseDate(String dateStr) {
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido, se espera YYYY-MM-DD");
        }
    }

    private tarjeta findTarjeta(String numeroTarjeta) {
        if (numeroTarjeta != null && !numeroTarjeta.trim().isEmpty()) {
            return tarjetaRepository.find("numeroTarjeta", numeroTarjeta).firstResult();
        }
        return null;
    }

    private recursoAsignado findRecursoAsignado(tarjeta tarjeta) {
        if (tarjeta != null) {
            return recursoAsignadoRepository
                .find("tarjeta.tarjetaId = ?1 and estado = ?2", 
                    tarjeta.getTarjetaId(), "Activo")
                .firstResult();
        }
        return null;
    }*/







}
