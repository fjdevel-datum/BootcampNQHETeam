package com.easycheck.infrastructure.ocr.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
// Renombra la clase para que refleje el nuevo servicio
public class Extractor { 

    // Inyecta el nuevo servicio
    @Inject
    GeminiService geminiService; 

    public String generateGastoJson(String ocrText) {
        
        String prompt = buildGeminiPrompt(ocrText);
        
        // La respuesta de Gemini ya es un JSON válido debido al JSON Mode
        String jsonOutput = geminiService.generateText(prompt);
        
        // Simplemente devolver la salida (la lógica de error se maneja en GeminiService)
        return jsonOutput;
    }

    private String buildGeminiPrompt(String ocrText) {
        
        // ... (Tu definición de extractionSchema, igual que antes) ...
        String extractionSchema = 
            "{" +
            "  \"Nombre_Pagador\": \"\"," +
            "  \"Fecha\": \"YYYY-MM-DD\"," +
            "  \"Monto_Total\": 0.00," +
            "  \"Numero_Tarjeta\": \"\"," + 
            "  \"Descripcion_Item\": \"\"," +
            "  \"Cantidad_Item\": 0" +   
            "}";

        // Instrucciones directas. NO se necesitan delimitadores.
        String instructions = 
            "You are a meticulous data extractor. Your SOLE TASK is to analyze the 'OCR TEXT' and return ONLY a valid JSON object matching the 'REQUIRED EXTRACTION SCHEMA'. " +
            "The output MUST strictly adhere to the provided JSON schema. " +
            "Perform all required data cleaning and conversions (Date to YYYY-MM-DD, Monto_Total to float, etc.).\n\n" +
            
            "REQUIRED EXTRACTION SCHEMA:\n" + extractionSchema + "\n\n" +
            "OCR TEXT:\n" + ocrText;
            
        return instructions;
    }
    
    // El método cleanJson se puede eliminar o comentar, ya no es necesario.
}