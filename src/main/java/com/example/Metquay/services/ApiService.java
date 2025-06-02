package com.example.Metquay.services;

import com.example.Metquay.dtos.AuthRequestDto;
import com.example.Metquay.dtos.AuthResponse;
import com.example.Metquay.dtos.BlogResponse;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApiService {

    private final RestTemplate restTemplate;

    @Value("${server.port}")
    private String serverPort;

    public ApiService() {
        this.restTemplate = new RestTemplate();
    }

    private String getJwtToken() {
        return (String) VaadinSession.getCurrent().getAttribute("jwtToken");
    }

    private void setJwtToken(String token) {
        VaadinSession.getCurrent().setAttribute("jwtToken", token);
    }

    private <T> HttpEntity<T> createEntityWithHeaders(T body) {
        HttpHeaders headers = new HttpHeaders();
        String token = getJwtToken();
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
    }

    public AuthResponse register(AuthRequestDto request) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/v1/auth/register";
        try {
            HttpEntity<AuthRequestDto> entity = new HttpEntity<>(request);
            ResponseEntity<AuthResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, AuthResponse.class);
            AuthResponse authResponse = response.getBody();
            if (authResponse != null && response.getStatusCode().is2xxSuccessful()) {
                setJwtToken(authResponse.getToken());
            }
            return authResponse;
        } catch (HttpClientErrorException e) {
            throw new Exception("Registration failed: " + e.getResponseBodyAsString());
        }
    }

    public AuthResponse login(AuthRequestDto request) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/v1/auth/login";
        try {
            HttpEntity<AuthRequestDto> entity = new HttpEntity<>(request);
            ResponseEntity<AuthResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, AuthResponse.class);
            AuthResponse authResponse = response.getBody();
            if (authResponse != null && response.getStatusCode().is2xxSuccessful()) {
                setJwtToken(authResponse.getToken());
            }
            return authResponse;
        } catch (HttpClientErrorException e) {
            throw new Exception("Login failed: " + e.getResponseBodyAsString());
        }
    }

    public List<BlogResponse> getPosts() throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts";
        try {
            HttpEntity<Void> entity = createEntityWithHeaders(null);
            ResponseEntity<BlogResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, BlogResponse[].class);
            BlogResponse[] posts = response.getBody();
            return posts != null ? Arrays.asList(posts) : List.of();
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to fetch posts: " + e.getResponseBodyAsString());
        }
    }

    public BlogResponse getPostById(UUID id) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts/" + id;
        try {
            HttpEntity<Void> entity = createEntityWithHeaders(null);
            ResponseEntity<BlogResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, BlogResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to fetch post with ID " + id + ": " + e.getResponseBodyAsString());
        }
    }

    public void createPost(BlogResponse post) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts";
        try {
            HttpEntity<BlogResponse> entity = createEntityWithHeaders(post);
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to create post: " + e.getResponseBodyAsString());
        }
    }

    public BlogResponse updatePost(BlogResponse post) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts";
        try {
            HttpEntity<BlogResponse> entity = createEntityWithHeaders(post);
            ResponseEntity<BlogResponse> response = restTemplate.exchange(url, HttpMethod.PUT, entity, BlogResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to update post: " + e.getResponseBodyAsString());
        }
    }

    public void deletePost(UUID id) throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts/" + id;
        try {
            HttpEntity<Void> entity = createEntityWithHeaders(null);
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to delete post with ID " + id + ": " + e.getResponseBodyAsString());
        }
    }

    public String getCurrentUserEmail() throws Exception {
        String url = "http://localhost:" + serverPort + "/api/posts/current-user";
        try {
            HttpEntity<Void> entity = createEntityWithHeaders(null);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> userDetails = response.getBody();
            if (userDetails != null && userDetails.containsKey("email")) {
                return (String) userDetails.get("email");
            }
            throw new Exception("Email not found in user details");
        } catch (HttpClientErrorException e) {
            throw new Exception("Failed to fetch current user: " + e.getResponseBodyAsString());
        }
    }

    public void logout() {
        setJwtToken(null);
    }

    public boolean isLoggedIn() {
        return getJwtToken() != null;
    }
}