package com.example.Metquay.views;

import com.example.Metquay.dtos.BlogResponse;
import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route(value = "posts", layout = MainLayout.class)
@PageTitle("Posts")
public class PostsView extends VerticalLayout implements BeforeEnterObserver {

    private final ApiService apiService;
    private VerticalLayout postsContainer;

    public PostsView(ApiService apiService) {
        this.apiService = apiService;

        setSizeFull();
        getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)").set("padding", "20px");

        initializeLayout();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!apiService.isLoggedIn()) {
            handleError("Unauthorized access. Please log in.", "login");
            return;
        }
        loadPosts();
    }

    private void initializeLayout() {
        H2 title = new H2("Explore Blog Posts");
        title.getStyle()
                .set("color", "#ffffff") // Already white, kept for clarity
                .set("text-align", "center")
                .set("font-size", "2.5rem")
                .set("margin-bottom", "30px");

        Button createPostButton = new Button("Create New Post", e -> getUI().ifPresent(ui -> ui.navigate("create-post")));
        styleButton(createPostButton);
        HorizontalLayout headerLayout = new HorizontalLayout(createPostButton);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.END);
        headerLayout.getStyle().set("margin-bottom", "20px");

        postsContainer = new VerticalLayout();
        postsContainer.setWidthFull();
        postsContainer.setAlignItems(Alignment.CENTER);
        postsContainer.setSpacing(true);

        add(title, headerLayout, postsContainer);
    }

    private void loadPosts() {
        try {
            List<BlogResponse> posts = apiService.getPosts();
            postsContainer.removeAll();

            if (posts.isEmpty()) {
                VerticalLayout emptyState = new VerticalLayout();
                emptyState.setAlignItems(Alignment.CENTER);
                H2 emptyMessage = new H2("No Posts Yet");
                emptyMessage.getStyle().set("color", "#ffffff").set("text-align", "center"); // Changed to white
                Paragraph suggestion = new Paragraph("Why not create your first post?");
                suggestion.getStyle().set("color", "#d1d5db").set("text-align", "center"); // Already light gray
                Button createButton = new Button("Write a Post", e -> getUI().ifPresent(ui -> ui.navigate("create-post")));
                styleButton(createButton);
                emptyState.add(emptyMessage, suggestion, createButton);
                postsContainer.add(emptyState);
            } else {
                for (BlogResponse post : posts) {
                    postsContainer.add(createPostCard(post));
                }
            }
        } catch (Exception e) {
            handleError("Failed to load posts: " + e.getMessage(), "login");
        }
    }

    private VerticalLayout createPostCard(BlogResponse post) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("600px");
        card.setPadding(true);
        card.getStyle()
                .set("background", "#2d2d2d")
                .set("border-radius", "12px")
                .set("box-shadow", "0 5px 20px rgba(0,0,0,0.5)")
                .set("transition", "transform 0.3s ease, box-shadow 0.3s ease")
                .set("cursor", "pointer");

        card.addClickListener(e -> {
            if (post.getId() != null) {
                getUI().ifPresent(ui -> ui.navigate("post/" + post.getId().toString()));
            }
        });

        card.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.transform = 'translateY(-5px)';" +
                        "  this.style.boxShadow = '0 10px 30px rgba(0,0,0,0.7)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = '0 5px 20px rgba(0,0,0,0.5)';" +
                        "});"
        );

        H3 title = new H3(post.getTitle());
        title.getStyle().set("color", "#ffffff").set("margin", "0"); // Already white

        Paragraph snippet = new Paragraph(truncateContent(post.getContent(), 150));
        snippet.getStyle().set("color", "#d1d5db").set("font-size", "1rem").set("margin", "10px 0"); // Already light gray

        Paragraph author = new Paragraph("By " + (post.getAuthor() != null ? post.getAuthor().getName() : "Unknown"));
        author.getStyle().set("color", "#d1d5db").set("font-style", "italic").set("font-size", "0.9rem"); // Changed to light gray

        Paragraph date = new Paragraph("Published on " + (post.getCreatedAt() != null ? post.getCreatedAt().toString() : "Unknown"));
        date.getStyle().set("color", "#d1d5db").set("font-size", "0.9rem"); // Already light gray

        HorizontalLayout metaLayout = new HorizontalLayout(author, date);
        metaLayout.setWidthFull();
        metaLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        card.add(title, snippet, metaLayout);
        return card;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }

    private void handleError(String message, String redirectRoute) {
        Notification.show(message);
        if (redirectRoute != null) {
            getUI().ifPresent(ui -> ui.navigate(redirectRoute));
        }
    }

    private void styleButton(Button button) {
        button.getStyle()
                .set("border-radius", "12px")
                .set("padding", "12px 20px")
                .set("background", "#4db6ac")
                .set("color", "#ffffff")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#26a69a';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.5)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '#4db6ac';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}