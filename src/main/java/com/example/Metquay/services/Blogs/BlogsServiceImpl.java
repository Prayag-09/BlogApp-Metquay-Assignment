package com.example.Metquay.services.Blogs;

import com.example.Metquay.dtos.CreatePostRequest;
import com.example.Metquay.dtos.UpdatePostRequest;
import com.example.Metquay.model.Blogs;
import com.example.Metquay.model.User;
import com.example.Metquay.repository.BlogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author prayagtushar
 */
@Service
@RequiredArgsConstructor
public class BlogsServiceImpl implements BlogServices {

    private final BlogRepository postRepository;

    @Override
    public List<Blogs> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    @Transactional
    public Blogs createPost(User user, CreatePostRequest createPostRequest) {
        Blogs newPost = Blogs.builder()
                .title(createPostRequest.getTitle())
                .subtitle(createPostRequest.getSubtitle())
                .content(createPostRequest.getContent())
                .author(user.getEmail())
                .build();
        return postRepository.save(newPost);
    }

    @Override
    @Transactional
    public Blogs updatePost(UpdatePostRequest updatePostRequest) {
        Blogs existingPost = postRepository.findById(updatePostRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post does not exist with ID " + updatePostRequest.getId()));
        existingPost.setTitle(updatePostRequest.getTitle());
        existingPost.setSubtitle(updatePostRequest.getSubtitle());
        existingPost.setContent(updatePostRequest.getContent());
        return postRepository.save(existingPost);
    }

    @Override
    public Blogs getPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post does not exist with ID " + id));
    }

    @Override
    @Transactional
    public void deletePost(UUID id) {
        Blogs post = getPost(id);
        postRepository.delete(post);
    }
}