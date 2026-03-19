package com.summarizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.summarizer.dto.Response;
import com.summarizer.repository.TextContentRepository;
import com.summarizer.utility.GeminiAIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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


        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        Map<String, Object> request = new HashMap<>();
        request.put("contents", new Object[]{
                new HashMap<String, Object>() {{
                    put("parts", new Object[]{
                            new HashMap<String, String>() {{
                                put("text", "Extract all text from this image.");
                            }},
                            new HashMap<String, Object>() {{
                                put("inline_data", new HashMap<String, String>() {{
                                    put("mime_type", file.getContentType());
                                    put("data", base64Image);
                                }});
                            }}
                    });
                }}
        });


        System.out.println("request- " + request);

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> responseEntity = geminiAIClient.sendRequestBody(request);
        com.summarizer.dto.Response response = objectMapper.readValue(responseEntity.getBody().getBytes(), Response.class);
        System.out.println("@@@@response is : " + response.toString());
        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
    }
}
