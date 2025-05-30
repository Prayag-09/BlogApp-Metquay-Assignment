package com.example.Metquay.views;

import com.example.Metquay.model.BlogPost;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

@Route(value = "blogs", layout = MainLayout.class)
@AnonymousAllowed
public class BlogsListView extends VerticalLayout {

    private final Grid<BlogPost> grid = new Grid<>(BlogPost.class, false);

    public BlogsListView() {
        setSizeFull();

        H2 title = new H2("All Blog Posts");
        title.getStyle().set("margin-bottom", "1rem");

        // Configure grid columns
        grid.addColumn(BlogPost::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(BlogPost::getSubtitle).setHeader("Subtitle").setAutoWidth(true);
        grid.addColumn(BlogPost::getAuthor).setHeader("Author").setAutoWidth(true);
        grid.addColumn(BlogPost::getDate).setHeader("Date").setAutoWidth(true);

        grid.setSizeFull();

        add(title, grid);
        loadBlogPosts();
    }

    private void loadBlogPosts() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            BlogPost[] posts = restTemplate.getForObject("http://localhost:8080/api/posts", BlogPost[].class);
            if (posts != null && posts.length > 0) {
                List<BlogPost> blogPostList = Arrays.asList(posts);
                grid.setItems(blogPostList);
            } else {
                // Add some sample data if no posts found
                createSampleData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Failed to load blog posts from API. Showing sample data.",
                    3000, Notification.Position.TOP_CENTER);
            createSampleData();
        }
    }

    private void createSampleData() {
        // Create sample blog posts for demonstration
        BlogPost post1 = new BlogPost();
        post1.setTitle("Getting Started with Vaadin");
        post1.setSubtitle("A beginner's guide to building web applications");
        post1.setAuthor("John Doe");
        post1.setDate(LocalDate.now().minusDays(5));

        BlogPost post2 = new BlogPost();
        post2.setTitle("Spring Security Integration");
        post2.setSubtitle("Securing your Vaadin application");
        post2.setAuthor("Jane Smith");
        post2.setDate(LocalDate.now().minusDays(10));

        BlogPost post3 = new BlogPost();
        post3.setTitle("REST API Development");
        post3.setSubtitle("Building robust APIs with Spring Boot");
        post3.setAuthor("Bob Johnson");
        post3.setDate(LocalDate.now().minusDays(15));

        List<BlogPost> samplePosts = Arrays.asList(post1, post2, post3);
        grid.setItems(samplePosts);
    }
}