package com.example.Metquay.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blogs {
    private UUID id;
    private String title;
    private String content;
    private Author author;
    private Integer readingTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}