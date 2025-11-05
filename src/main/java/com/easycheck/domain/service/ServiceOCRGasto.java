package com.easycheck.domain.service;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.OCRGastoRequestDTO;
import com.easycheck.domain.model.actividad;
import com.easycheck.domain.model.factura;
import com.easycheck.domain.model.gasto;
import com.easycheck.domain.model.moneda;
import com.easycheck.domain.model.recursoAsignado;
import com.easycheck.domain.model.tipoGasto;
import com.easycheck.infrastructure.ocr.OCRService;
import com.easycheck.infrastructure.ocr.service.Extractor;
import com.easycheck.infrastructure.repository.ActividadRepository;
import com.easycheck.infrastructure.repository.FacturaRepository;
import com.easycheck.infrastructure.repository.GastoRepository;
import com.easycheck.infrastructure.repository.RecursoAsignadoRepository;
import com.easycheck.infrastructure.repository.monedaRepository;
import com.easycheck.infrastructure.repository.tipoGastoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.json.JSONObject;

import com.easycheck.infrastructure.openkm.OpenKMService; // Importa tu nuevo servicio
import java.io.ByteArrayInputStream; // Importa esto
import java.io.IOException; // Importa esto

@ApplicationScoped
public class ServiceOCRGasto {
    
    @Inject
    OCRService ocrService;

    @Inject
    Extractor extractor;
    
    @Inject    
    GastoRepository gastoRepository;

    @Inject
    monedaRepository monedaRepository;


    @Inject
    FacturaRepository facturaRepository;

    @Inject
    RecursoAsignadoRepository recursoAsignadoRepository;

    @Inject
    tipoGastoRepository tipoGastoRepository;

    @Inject
    ActividadRepository actividadRepository;
    
    @Inject
    OpenKMService openKMService;
    

    public GastoDTO procesarFacturayGuardarGasto(OCRGastoRequestDTO dto) throws IOException
    {
        // ---------------------------------------------------------------------
        //  Paso CRÍTICO: Leer el InputStream a un byte[]
        //  Un InputStream solo se puede leer UNA VEZ. Como lo necesitamos 2 veces
        //  (1 para OpenKM, 1 para OCR), lo pasamos a un array de bytes.
        // ---------------------------------------------------------------------
        byte[] fileBytes = dto.file.readAllBytes();
        dto.file.close(); // Cerramos el stream original

        // 1️⃣ Guardar en OpenKM PRIMERO
        String comprobantePath = openKMService.uploadInvoice(new ByteArrayInputStream(fileBytes));

        // 2️⃣ Crear la entidad Factura en nuestra DB
        factura newFactura = new factura();
        newFactura.setComprobante(comprobantePath); // Asumo que tienes un setComprobante(String path)
        facturaRepository.persist(newFactura);
        // ¡Ya tenemos el nuevo facturaId! (newFactura.getFacturaId())

        // 3️⃣ Extraer texto con Azure OCR (usando el segundo stream)
        String extractedText = ocrService.extractText(new ByteArrayInputStream(fileBytes));

        // 4️⃣ Obtener JSON estructurado desde Gemini
        String structuredJson = extractor.generateGastoJson(extractedText);
        JSONObject json = new JSONObject(structuredJson);

        // 5️⃣ Validar y obtener las entidades necesarias
        moneda moneda = monedaRepository.findById(dto.monedaId);
        // Ya no buscamos la factura, usamos la que acabamos de crear:
        factura factura = newFactura; 
        recursoAsignado recurso = recursoAsignadoRepository.findById(dto.recursoId);
        tipoGasto tipoGasto = tipoGastoRepository.findById(dto.tipoGastoId);
        actividad actividad = actividadRepository.findById(dto.actividadId);

        // Ya no necesitamos validar la factura
        if (moneda == null || recurso == null || tipoGasto == null || actividad == null) {
            throw new IllegalArgumentException("Alguna entidad relacionada no fue encontrada.");
        }

        if (moneda == null || factura == null || recurso == null || tipoGasto == null || actividad == null) {
            throw new IllegalArgumentException("Alguna entidad relacionada no fue encontrada.");
        }

        // 4️⃣ Parsear fecha desde JSON Gemini
        Date fechaGasto;
        try {
            LocalDate localDate = LocalDate.parse(json.getString("Fecha"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            fechaGasto = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha del JSON inválida: " + e.getMessage());
        }

        // 5️⃣ Crear entidad gasto
        gasto nuevoGasto = new gasto();
        nuevoGasto.setFecha(fechaGasto);
        nuevoGasto.setMonedaGasto(moneda);
        nuevoGasto.setFactura(factura);
        nuevoGasto.setRecursoAsignado(recurso);
        nuevoGasto.setTipoGasto(tipoGasto);
        nuevoGasto.setActividad(actividad);
        nuevoGasto.setDescripcionGasto(json.optString("Descripcion_Item", "Gasto sin descripción"));
        nuevoGasto.setTotalGasto(json.optDouble("Monto_Total", 0.0));
        nuevoGasto.setTotalMonedaBase(json.optDouble("Monto_Total", 0.0));

        gastoRepository.persist(nuevoGasto);

        // 6️⃣ Retornar DTO
        return new GastoDTO(
            nuevoGasto.getGastoId(),
            json.getString("Fecha"),
            moneda.getMonedaId(),
            factura.getFacturaId(),
            recurso.getRecursoId(),
            tipoGasto.getTipoGastoId(),
            actividad.getActividadId(),
            nuevoGasto.getDescripcionGasto(),
            nuevoGasto.getTotalGasto(),
            nuevoGasto.getTotalMonedaBase()
        );
    }
    





}
