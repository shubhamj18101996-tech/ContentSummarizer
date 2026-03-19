package com.summarizer.entity;

import jakarta.persistence.*;

@Entity
public class TextContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String originalText;

    @Column(columnDefinition = "TEXT")
    private String summarizedText;

    private String keywords;
    private String sentiment;

    // Constructors
    public TextContent() {
    }

    public TextContent(String originalText, String summarizedText, String keywords, String sentiment) {
        this.originalText = originalText;
        this.summarizedText = summarizedText;
        this.keywords = keywords;
        this.sentiment = sentiment;
    }

    // Getters & Setters
}

