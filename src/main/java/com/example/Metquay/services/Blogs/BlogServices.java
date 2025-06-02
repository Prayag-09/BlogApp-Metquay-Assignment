package com.example.Metquay.services.Blogs;

import com.example.Metquay.dtos.CreatePostRequest;
import com.example.Metquay.dtos.UpdatePostRequest;
import com.example.Metquay.model.Blogs;
import com.example.Metquay.model.User;

import java.util.List;
import java.util.UUID;

/**
 * @author prayagtushar
 */
public interface BlogServices {
    List<Blogs> getAllPosts();
    Blogs createPost(User user, CreatePostRequest createPostRequest);
    Blogs updatePost(UpdatePostRequest updatePostRequest);
    Blogs getPost(UUID id);
    void deletePost(UUID id);
}