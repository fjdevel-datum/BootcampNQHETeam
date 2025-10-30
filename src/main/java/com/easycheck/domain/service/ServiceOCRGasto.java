package com.easycheck.domain.service;

import com.easycheck.application.dto.GastoDTO;
import com.easycheck.application.dto.OCRGastoRequestDTO;
import com.easycheck.infrastructure.ocr.OCRService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.json.JSONObject;

@ApplicationScoped
public class ServiceOCRGasto {
    
    @Inject
    OCRService ocrService;

    
}
