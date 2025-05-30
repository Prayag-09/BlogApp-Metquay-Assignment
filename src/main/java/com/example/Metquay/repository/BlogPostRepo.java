package com.example.Metquay.repository;

import com.example.Metquay.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BlogPostRepo extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByAuthor(String author);
    List<BlogPost> findByTitleContainingIgnoreCase(String keyword);
    List<BlogPost> findByDate(LocalDate date);
    List<BlogPost> findTop5ByOrderByCreatedAtDesc();
}