package com.example.Metquay.controller;

import com.example.Metquay.model.BlogPost;
import com.example.Metquay.repository.BlogPostRepo;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {

    private final BlogPostRepo repo;

    public BlogPostController(BlogPostRepo repo) {
        this.repo = repo;
    }

    @PostMapping
    @RolesAllowed("USER")
    public ResponseEntity<BlogPost> create(@RequestBody BlogPost post) {
        BlogPost savedPost = repo.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping
    public ResponseEntity<List<BlogPost>> getAll() {
        List<BlogPost> posts = repo.findAll();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPost> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @RolesAllowed("USER")
    public ResponseEntity<BlogPost> update(@PathVariable Long id, @RequestBody BlogPost updatedPost) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedPost.getTitle());
                    existing.setSubtitle(updatedPost.getSubtitle());
                    existing.setContent(updatedPost.getContent());
                    BlogPost saved = repo.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("USER")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}