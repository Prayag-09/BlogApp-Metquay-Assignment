package com.example.Metquay.controller;

import com.example.Metquay.dtos.Author;
import com.example.Metquay.dtos.CreatePostRequest;
import com.example.Metquay.dtos.UpdatePostRequest;
import com.example.Metquay.dtos.BlogResponse;
import com.example.Metquay.model.Blogs;
import com.example.Metquay.model.User;
import com.example.Metquay.repository.UserRepo;
import com.example.Metquay.services.Blogs.BlogServices;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author prayagtushar
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final BlogServices blogService;
    private final UserRepo userRepo;

    @Autowired
    public PostController(BlogServices blogService, UserRepo userRepo) {
        this.blogService = blogService;
        this.userRepo = userRepo;
    }

    @PostMapping
    public ResponseEntity<BlogResponse> createPost(@Valid @RequestBody CreatePostRequest request, HttpServletRequest httpRequest) {
        User user = getCurrentUser(httpRequest);
        Blogs post = blogService.createPost(user, request);
        return ResponseEntity.ok(mapToBlogResponse(post));
    }

    @GetMapping
    public ResponseEntity<List<BlogResponse>> getAllPosts() {
        List<BlogResponse> posts = blogService.getAllPosts().stream()
                .map(this::mapToBlogResponse)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getPostById(@PathVariable UUID id) {
        Blogs post = blogService.getPost(id);
        return ResponseEntity.ok(mapToBlogResponse(post));
    }

    @PutMapping
    public ResponseEntity<BlogResponse> updatePost(@Valid @RequestBody UpdatePostRequest request, HttpServletRequest httpRequest) {
        Blogs post = blogService.getPost(request.getId());
        User currentUser = getCurrentUser(httpRequest);
        if (!post.getAuthor().equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        Blogs updatedPost = blogService.updatePost(request);
        return ResponseEntity.ok(mapToBlogResponse(updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id, HttpServletRequest httpRequest) {
        Blogs post = blogService.getPost(id);
        User currentUser = getCurrentUser(httpRequest);
        if (!post.getAuthor().equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        blogService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getCurrentUserDetails(HttpServletRequest httpRequest) {
        User user = getCurrentUser(httpRequest);
        return ResponseEntity.ok(user);
    }

    private User getCurrentUser(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        return userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private BlogResponse mapToBlogResponse(Blogs blog) {
        User user = userRepo.findByEmail(blog.getAuthor()).orElse(null);
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .subtitle(blog.getSubtitle())
                .content(blog.getContent())
                .author(user != null ? Author.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build() : null)
                .readingTime(blog.getReadingTime())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}