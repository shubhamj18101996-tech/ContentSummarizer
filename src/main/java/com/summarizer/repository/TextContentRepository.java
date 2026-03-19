package com.summarizer.repository;

import com.summarizer.entity.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextContentRepository extends JpaRepository<TextContent, Long> {
}
