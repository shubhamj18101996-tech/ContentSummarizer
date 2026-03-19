package com.summarizer.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiAIClient {

    // Groq API endpoint (OpenAI compatible)
    @Value("${groq.api.url}")
    private String API_URL;

    // Get your free API key from: https://groq.com/
    @Value("${groq.api.key}")
    private String API_KEY;

    public String sendRequest(String input) throws IOException {
        Map<String, Object> part = new HashMap<>();
        part.put("text", input);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("contents", List.of(content));
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> responseFromGemini = sendRequestBody(requestBodyMap);

        System.out.println("response - "+ responseFromGemini);



        Map<String, Object> parsedData = parseApiResponse(responseFromGemini);
        String summary= "";
        if ((Boolean) parsedData.get("success")) {
             summary = (String) parsedData.get("content");
        }

        System.out.println("@@@@response is : " + summary);
        return summary;
    }

    public static Map<String, Object> parseApiResponse(ResponseEntity<String> response) {
        Map<String, Object> result = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            // Extract main content only
            String content = rootNode.at("/choices/0/message/content").asText();

            result.put("success", true);
            result.put("content", content);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    public ResponseEntity<String> sendRequestBody(Map<String, Object> requestBodyMap) throws JsonProcessingException {
        // Define the endpoint URL




        // Convert Gemini format (contents) to Groq/OpenAI format (messages)
        Map<String, Object> groqBody = new java.util.HashMap<>();

        // Extract text from Gemini format
        String textContent = "";
        if (requestBodyMap.containsKey("contents")) {
            List<?> contents = (List<?>) requestBodyMap.get("contents");
            if (!contents.isEmpty()) {
                Map<String, Object> contentMap = (Map<String, Object>) contents.get(0);
                List<?> parts = (List<?>) contentMap.get("parts");
                if (!parts.isEmpty()) {
                    Map<String, Object> part = (Map<String, Object>) parts.get(0);
                    textContent = (String) part.get("text");
                }
            }
        }

        // Build Groq/OpenAI format request
        groqBody.put("model", "llama-3.3-70b-versatile");
        groqBody.put("messages", List.of(
                Map.of("role", "user", "content", textContent)
        ));

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + API_KEY);
        // Get free API key from: https://groq.com/

        // Convert the request body map to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(groqBody);

        // Combine headers and body
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        System.out.println("Entity "+entity);

        RestTemplate restTemplate = new RestTemplate();

        // Send request
        return restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                String.class
        );
    }


    /**
     * Send request using Groq API (Free)
     * Models available: mixtral-8x7b-32768, llama-3.1-70b-versatile, llama-3.1-8b-instant
     */
    private String sendGroqRequest(String input, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", "mixtral-8x7b-32768",  // Free Groq model (very fast)
                "messages", List.of(
                        Map.of("role", "user", "content", prompt + "\n\n" + input)
                ),
                "temperature", 0.7,
                "max_tokens", 1024
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(60))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);
        return response.getBody();
    }
}
