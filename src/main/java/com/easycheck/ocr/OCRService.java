package com.easycheck.ocr;

import java.io.File;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OCRService {

    public String procesarImagen(File imagen) {
        ITesseract tesseract = new Tesseract();

        // Ruta al directorio de "tessdata" si es necesario (solo si Tesseract no la encuentra)
        tesseract.setDatapath("C:\\Users\\samus\\AppData\\Local\\Programs\\Tesseract-OCR\\tessdata");

        // Idioma: español (usa "eng" para inglés)
        tesseract.setLanguage("spa");

        try {
            return tesseract.doOCR(imagen);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "Error procesando imagen: " + e.getMessage();
        }
    }
    public static void main(String[] args) {
    OCRService ocr = new OCRService();
    File imagen = new File("C:\\Users\\samus\\OneDrive\\Pictures\\Screenshots\\factura.png"); // cambia la ruta
    System.out.println(ocr.procesarImagen(imagen));
}
}
