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
        getStyle().set("background", "#f4f4f5").set("padding", "20px");

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
                .set("color", "#333333")
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
                emptyMessage.getStyle().set("color", "#333333").set("text-align", "center");
                Paragraph suggestion = new Paragraph("Why not create your first post?");
                suggestion.getStyle().set("color", "#666666").set("text-align", "center");
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
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
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
                        "  this.style.boxShadow = '0 6px 16px rgba(0,0,0,0.15)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)';" +
                        "});"
        );

        H3 title = new H3(post.getTitle());
        title.getStyle().set("color", "#333333").set("margin", "0");

        Paragraph snippet = new Paragraph(truncateContent(post.getContent(), 150));
        snippet.getStyle().set("color", "#666666").set("font-size", "1rem").set("margin", "10px 0");

        Paragraph author = new Paragraph("By " + (post.getAuthor() != null ? post.getAuthor().getName() : "Unknown"));
        author.getStyle().set("color", "#666666").set("font-style", "italic").set("font-size", "0.9rem");

        Paragraph date = new Paragraph("Published on " + (post.getCreatedAt() != null ? post.getCreatedAt().toString() : "Unknown"));
        date.getStyle().set("color", "#666666").set("font-size", "0.9rem");

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
        Notification notification = new Notification(message);
        notification.getElement().getStyle()
                .set("background", "#007bff")
                .set("color", "#ffffff")
                .set("border-radius", "8px")
                .set("padding", "10px");
        notification.open();
        if (redirectRoute != null) {
            getUI().ifPresent(ui -> ui.navigate(redirectRoute));
        }
    }

    private void styleButton(Button button) {
        button.getStyle()
                .set("border-radius", "12px")
                .set("padding", "12px 20px")
                .set("background", "#007bff")
                .set("color", "#ffffff")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#0056b3';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '#007bff';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}