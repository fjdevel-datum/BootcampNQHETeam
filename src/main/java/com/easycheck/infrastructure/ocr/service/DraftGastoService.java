package com.easycheck.infrastructure.ocr.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DraftGastoService {
    
    private final Map<String, String> draftGastos = new ConcurrentHashMap<>();

   

    public String saveDraft(String geminiJson){
        String draftId = UUID.randomUUID().toString();
        draftGastos.put(draftId, geminiJson);
        return draftId;
    }

    

    public String getAndRemoveDraft(String draftId){
        return draftGastos.remove(draftId);
    }
}


