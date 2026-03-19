package com.summarizer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageMetadata {
    private int promptTokenCount;
    private int candidatesTokenCount;
    private int totalTokenCount;
    private List<TokenDetails> promptTokensDetails;
    private List<TokenDetails> candidatesTokensDetails;

    // Getters and Setters
    public int getPromptTokenCount() {
        return promptTokenCount;
    }

    public void setPromptTokenCount(int promptTokenCount) {
        this.promptTokenCount = promptTokenCount;
    }

    public int getCandidatesTokenCount() {
        return candidatesTokenCount;
    }

    public void setCandidatesTokenCount(int candidatesTokenCount) {
        this.candidatesTokenCount = candidatesTokenCount;
    }

    public int getTotalTokenCount() {
        return totalTokenCount;
    }

    public void setTotalTokenCount(int totalTokenCount) {
        this.totalTokenCount = totalTokenCount;
    }

    public List<TokenDetails> getPromptTokensDetails() {
        return promptTokensDetails;
    }

    public void setPromptTokensDetails(List<TokenDetails> promptTokensDetails) {
        this.promptTokensDetails = promptTokensDetails;
    }

    public List<TokenDetails> getCandidatesTokensDetails() {
        return candidatesTokensDetails;
    }

    public void setCandidatesTokensDetails(List<TokenDetails> candidatesTokensDetails) {
        this.candidatesTokensDetails = candidatesTokensDetails;
    }
}

