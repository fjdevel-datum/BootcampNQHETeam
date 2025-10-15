package com.easycheck.domain.ports;

import java.io.InputStream;
import java.net.URL;

public interface FacturaStoragePort {

    URL guardarFactura(InputStream contenido, String nombreArchivo,String mimeType);
    
}
