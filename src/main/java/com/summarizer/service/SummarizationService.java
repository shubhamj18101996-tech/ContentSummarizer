package com.summarizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.summarizer.repository.TextContentRepository;
import com.summarizer.utility.GeminiAIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class SummarizationService {

    @Autowired
    private TextContentRepository repository;

    @Autowired
    private GeminiAIClient geminiAIClient;

    public String askMe(String inputText, String type, String tone) throws IOException {
        String requestText = inputText + ". Please give answer in " + type + ". And in "+ tone + " tone.";
        System.out.println("request -" + requestText);
        return geminiAIClient.sendRequest(requestText);
    }

    public String simulateTranslation(String inputText, String lang) throws IOException {

        String requestText = inputText + ". Please translate this text to " + lang + ".";
        System.out.println("request -" + requestText);
        return geminiAIClient.sendRequest(requestText);
    }

    public String summarizeText(String inputText, String tone) throws IOException {

        String requestText = inputText + ". Please summarize this text. and keep it short. And in "+ tone + " tone.";
        System.out.println("request -" + requestText);
        return geminiAIClient.sendRequest(requestText);
    }


    public String getTextFromImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", "Extract all text from this image.");

        Map<String, Object> imagePart = new HashMap<>();
        Map<String, String> inlineData = new HashMap<>();
        inlineData.put("mime_type", file.getContentType());
        inlineData.put("data", base64Image);
        imagePart.put("inline_data", inlineData);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart, imagePart));

        Map<String, Object> request = new HashMap<>();
        request.put("contents", List.of(content));

        System.out.println("request- " + request);

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> responseEntity = geminiAIClient.sendRequestBody(request);

        // Check if response body is null
        if (responseEntity == null || responseEntity.getBody() == null || responseEntity.getBody().isEmpty()) {
            throw new IOException("Received empty response from Gemini AI Client");
        }

        String responseBody = responseEntity.getBody();
        System.out.println("Raw API Response: " + responseBody);

        // Parse the Groq/OpenAI format response (uses "choices" not "candidates")
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // Extract text from Groq format response
            if (rootNode.has("choices") && rootNode.get("choices").isArray() && rootNode.get("choices").size() > 0) {
                String textContent = rootNode.at("/choices/0/message/content").asText();
                if (textContent != null && !textContent.isEmpty()) {
                    System.out.println("Extracted text from image: " + textContent);
                    return textContent;
                } else {
                    throw new IOException("No text content found in API response");
                }
            } else {
                throw new IOException("Invalid API response format - no choices found");
            }
        } catch (Exception e) {
            String errorMsg = "Error parsing API response: " + e.getMessage();
            System.err.println(errorMsg);
            throw new IOException("Failed to extract text from image: " + e.getMessage(), e);
        }
    }
}
