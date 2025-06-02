package com.example.Metquay.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Metquay.model.Blogs;


/**
 *
 * @author prayagtushar
 */

@Repository
public interface BlogRepository extends JpaRepository<Blogs, UUID> {
    List<Blogs> findAllByAuthor(String author);
}