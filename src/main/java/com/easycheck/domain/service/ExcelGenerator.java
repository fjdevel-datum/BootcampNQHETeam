package com.easycheck.domain.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.easycheck.application.dto.DetalleGastoDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class ExcelGenerator {

    private static final String[] HEADERS = {
        "ID Empleado", "Nombre", "Rol", "Centro Costo", "Empresa",
        "Actividad", "Descripción Gasto", "Total", "Moneda", "Fecha Gasto",
        "ID Recurso", "ID Tarjeta", "Número Tarjeta"
    };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ByteArrayInputStream gastosToExcel(List<DetalleGastoDTO> gastos) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Detalle de Gastos");

            // Estilo para la cabecera
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Fila de cabecera
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Estilo para celdas de fecha
            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

            // Llenar datos
            int rowIdx = 1;
            for (DetalleGastoDTO gasto : gastos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(gasto.empleadoId());
                row.createCell(1).setCellValue(gasto.nombreCompleto());
                row.createCell(2).setCellValue(gasto.rol());
                row.createCell(3).setCellValue(gasto.nombreCentro());
                row.createCell(4).setCellValue(gasto.empresaNombre());
                row.createCell(5).setCellValue(gasto.nombreActividad());
                row.createCell(6).setCellValue(gasto.descripcionGasto());
                row.createCell(7).setCellValue(gasto.totalGasto().doubleValue()); // POI maneja mejor doubles
                row.createCell(8).setCellValue(gasto.simboloMoneda());
                
                // Formatear la fecha
                Cell dateCell = row.createCell(9);
                dateCell.setCellValue(gasto.fechaGasto());
                dateCell.setCellStyle(dateCellStyle);

                row.createCell(10).setCellValue(gasto.recursoId());
                row.createCell(11).setCellValue(gasto.tarjetaId());
                row.createCell(12).setCellValue(gasto.numeroTarjeta());
            }

            // Auto-ajustar columnas
            for(int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}