package com.summarizer.controller;

import com.summarizer.dto.AskMeRequest;
import com.summarizer.dto.TranslationRequest;
import com.summarizer.service.PdfService;
import com.summarizer.service.SummarizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8080", "https://contentsummarizer-54xi.onrender.com"})
public class SummarizationController {

    @Autowired
    private SummarizationService service;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/summarize")
    public Map<String, String> summarize(@RequestBody AskMeRequest request) throws IOException {
        System.out.println("inside controller " + request);
        String detailedText = service.askMe(request.getInputText(), request.getType(), request.getTone());
        Map<String, String> response = new HashMap<>();
        response.put("detailedText", detailedText);
        return response;
    }

    @PostMapping("/translate")
    public Map<String, String> translateText(@RequestBody TranslationRequest request) throws IOException {
        String text = request.getText();
        String language = request.getLanguage();

        // Simulated translation logic (replace with actual translation service or library)
        String translatedText = service.simulateTranslation(text, language);

        // Build and return the response
        Map<String, String> response = new HashMap<>();
        response.put("originalText", text);
        response.put("targetLanguage", language);
        response.put("translatedText", translatedText);

        return response;
    }

    @PostMapping("/summarizeBig")
    public Map<String, String> summarizeBig(@RequestBody AskMeRequest requestBody) throws IOException {

        System.out.println("inside controller " + requestBody.getInputText());
        String detailedText = service.summarizeText(requestBody.getInputText(), requestBody.getTone());
        Map<String, String> response = new HashMap<>();
        response.put("detailedText", detailedText);
        return response;
    }

    @PostMapping("/pdf/generate")
    public ResponseEntity<InputStreamResource> generatePdf(@RequestBody String text) throws IOException {
        System.out.println("pdf api - " + text);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        headers.setContentDispositionFormData("attachment", "downloaded.pdf");


        InputStreamResource isr = pdfService.createPdf(text, "downloaded.pdf");

        return new ResponseEntity<>(isr, headers, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println("inside upload image " + file);
        return service.getTextFromImage(file);
    }


}