package com.easycheck.infrastructure.ocr.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@ApplicationScoped
public class GeminiService {
    
    private static final Logger LOG = Logger.getLogger(GeminiService.class.getName());
    
    // CORRECCIÓN 1: Usar el endpoint v1beta, que suele tener mejor soporte para JSON Mode
    private static final String GEMINI_MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    @ConfigProperty(name = "gemini.api.key") 
    String apiKey;

    /**
     * Define el JSON Schema esperado para la extracción.
     * Esto es CRÍTICO para que el JSON Mode funcione.
     */
    private JSONObject getExtractionSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "OBJECT");
        
        JSONObject properties = new JSONObject();
        properties.put("Nombre_Pagador", new JSONObject().put("type", "STRING").put("description", "Full name of the person/entity who made the payment."));
        properties.put("Fecha", new JSONObject().put("type", "STRING").put("description", "Date of the expense in YYYY-MM-DD format."));
        properties.put("Monto_Total", new JSONObject().put("type", "NUMBER").put("description", "Total amount of the expense, as a decimal number."));
        properties.put("Numero_Tarjeta", new JSONObject().put("type", "STRING").put("description", "Last 4 digits or visible part of the card number."));
        properties.put("Descripcion_Item", new JSONObject().put("type", "STRING").put("description", "A concise, single-line description of the expense item."));
        properties.put("Cantidad_Item", new JSONObject().put("type", "INTEGER").put("description", "The quantity of the item purchased."));

        schema.put("properties", properties);
        schema.put("required", new String[]{"Nombre_Pagador", "Fecha", "Monto_Total", "Descripcion_Item", "Cantidad_Item"});
        
        return schema;
    }

    public String generateText(String prompt) {
        HttpURLConnection connection = null;
        try {
            LOG.info("Enviando prompt a Gemini 2.5 Flash (v1beta) con JSON Schema...");
            
            URL url = new URL(GEMINI_MODEL_URL + apiKey);
            connection = (HttpURLConnection) url.openConnection();
            
            // Configurar conexión
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            // Crear JSON del request (Formato Gemini)
            JSONObject generationConfig = new JSONObject(); 

            // CORRECCIÓN 2: Incluir responseMimeType Y responseSchema
            generationConfig.put("responseMimeType", "application/json"); 
            generationConfig.put("responseSchema", getExtractionSchema()); // Usamos el esquema que definimos
            
            JSONObject jsonContent = new JSONObject();
            jsonContent.put("text", prompt);

            JSONObject requestJson = new JSONObject();
            requestJson.put("contents", new JSONObject[]{
                new JSONObject().put("role", "user").put("parts", new JSONObject[]{jsonContent})
            });
            requestJson.put("generationConfig", generationConfig); 
            
            // Enviar request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Leer respuesta
            int responseCode = connection.getResponseCode();
            
            BufferedReader reader = (responseCode >= 200 && responseCode < 300)
                ? new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
                : new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return processGeminiResponse(response.toString(), responseCode);
            
        } catch (Exception e) {
            LOG.severe("Error al conectar con Gemini API: " + e.getMessage());
            return createErrorJson("Error interno al conectar con Gemini API: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    // Método para procesar la respuesta de la API de Gemini
    private String processGeminiResponse(String responseBody, int responseCode) {
        try {
            if (responseCode != 200) {
                 // Si no es 200, devolvemos el error del servidor
                 return createErrorJson("Error HTTP " + responseCode + " de Gemini: " + responseBody);
            }
            
            JSONObject jsonResponse = new JSONObject(responseBody);
            
            if (jsonResponse.has("candidates")) {
                JSONObject firstCandidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                if (content.has("parts")) {
                    JSONObject firstPart = content.getJSONArray("parts").getJSONObject(0);
                    if (firstPart.has("text")) {
                        // Con el responseSchema, el campo 'text' contendrá el JSON limpio y garantizado.
                        return firstPart.getString("text").trim();
                    }
                }
            }
            
            return createErrorJson("Respuesta de Gemini recibida, pero no contiene texto generado.");

        } catch (Exception e) {
            LOG.severe("Error al procesar respuesta JSON de Gemini: " + e.getMessage());
            return createErrorJson("Error al procesar la respuesta del modelo Gemini: " + e.getMessage());
        }
    }
    
    // Método auxiliar para estandarizar el error JSON
    private String createErrorJson(String message) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("error", message);
        errorJson.put("status", "GEMINI_SERVICE_FAILED");
        return errorJson.toString(); 
    }
}
