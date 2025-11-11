package com.easycheck.infrastructure.ocr;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.AnalyzeDocumentOptions;
import com.azure.ai.documentintelligence.models.AnalyzeOperationDetails;
import com.azure.ai.documentintelligence.models.AnalyzeResult;
import com.azure.ai.documentintelligence.models.DocumentPage;
import com.azure.ai.documentintelligence.models.DocumentWord;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.SyncPoller;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.InputStream;

@ApplicationScoped
public class OCRService {

    private DocumentIntelligenceClient client;

    @ConfigProperty(name = "azure.documentintelligence.endpoint")
    String endpoint;

    @ConfigProperty(name = "azure.documentintelligence.key")
    String key;

    @PostConstruct
    void init() {
        client = new DocumentIntelligenceClientBuilder()
                .credential(new AzureKeyCredential(key))
                .endpoint(endpoint)
                .buildClient();
    }

    // Recibe InputStream directamente
    public String extractText(InputStream imageStream) {
        try {
            BinaryData documentData = BinaryData.fromStream(imageStream);
            AnalyzeDocumentOptions options = new AnalyzeDocumentOptions(documentData);

            // Inicia el análisis usando el modelo prebuilt-read
            SyncPoller<AnalyzeOperationDetails, AnalyzeResult> poller =
                    client.beginAnalyzeDocument("prebuilt-read", options);

            AnalyzeResult result = poller.getFinalResult();

            StringBuilder extractedText = new StringBuilder();

            // Si Document Intelligence devuelve contenido textual directo
            if (result.getContent() != null && !result.getContent().isEmpty()) {
                extractedText.append(result.getContent());
            } 
            else if (result.getPages() != null) {
                for (DocumentPage page : result.getPages()) {
                    if (page.getWords() != null) {
                        for (DocumentWord word : page.getWords()) {
                            extractedText.append(word.getContent()).append(" ");
                        }
                    }
                    extractedText.append("\n");
                }
            }

            String text = extractedText.toString().trim();
            return text.isEmpty() ? "No se pudo extraer texto de la imagen" : text;

        } catch (Exception e) {
            return "Error al procesar la imagen con Azure OCR: " + e.getMessage();
        }
    }
}
