package com.example.Metquay.views;

import com.example.Metquay.dtos.BlogResponse;
import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.UUID;

@Route(value = "post/:id", layout = MainLayout.class)
@PageTitle("Post Details")
public class PostDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ApiService apiService;
    private BlogResponse post;
    private boolean isEditing = false;
    private Binder<BlogResponse> binder;
    private String currentUserEmail;

    public PostDetailView(ApiService apiService) {
        this.apiService = apiService;
        this.binder = new Binder<>(BlogResponse.class);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)").set("padding", "20px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!apiService.isLoggedIn()) {
            handleError("Unauthorized access. Please log in.", "login");
            return;
        }

        try {
            currentUserEmail = apiService.getCurrentUserEmail();
        } catch (Exception e) {
            handleError("Failed to fetch user details: " + e.getMessage(), "posts");
            return;
        }

        String postId = event.getRouteParameters().get("id").orElse(null);
        if (postId == null) {
            handleError("Invalid post ID.", "posts");
            return;
        }

        try {
            UUID id = UUID.fromString(postId);
            post = apiService.getPostById(id);
            if (post == null) {
                handleError("Post not found.", "posts");
                return;
            }
            renderPost();
        } catch (Exception e) {
            handleError("Failed to load post: " + e.getMessage(), "posts");
        }
    }

    private void renderPost() {
        removeAll();

        VerticalLayout card = new VerticalLayout();
        card.setWidth("600px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "#2d2d2d")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 5px 20px rgba(0,0,0,0.5)");

        if (isEditing) {
            renderEditForm(card);
        } else {
            renderViewMode(card);
        }

        add(card);
    }

    private void renderViewMode(VerticalLayout card) {
        H2 title = new H2(post.getTitle());
        title.getStyle().set("color", "#ffffff"); // Already white

        Paragraph subtitle = new Paragraph(post.getSubtitle() != null ? post.getSubtitle() : "");
        subtitle.getStyle().set("color", "#d1d5db").set("font-size", "1.1rem"); // Already light gray

        Paragraph content = new Paragraph(post.getContent());
        content.getStyle().set("color", "#d1d5db").set("white-space", "pre-wrap"); // Already light gray

        Paragraph author = new Paragraph("By " + (post.getAuthor() != null ? post.getAuthor().getName() : "Unknown"));
        author.getStyle().set("color", "#d1d5db").set("font-style", "italic"); // Already light gray

        Paragraph date = new Paragraph("Published on " + (post.getCreatedAt() != null ? post.getCreatedAt().toString() : "Unknown"));
        date.getStyle().set("color", "#d1d5db").set("font-size", "0.9rem"); // Already light gray

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        boolean isAuthor = post.getAuthor() != null && post.getAuthor().getEmail() != null && post.getAuthor().getEmail().equals(currentUserEmail);
        if (isAuthor) {
            Button editButton = new Button("Edit", e -> {
                isEditing = true;
                renderPost();
            });
            styleButton(editButton, true);

            Button deleteButton = new Button("Delete", e -> confirmDelete());
            styleButton(deleteButton, false);

            buttonLayout.add(editButton, deleteButton);
        }

        Button backButton = new Button("Back to Posts", e -> getUI().ifPresent(ui -> ui.navigate("posts")));
        styleButton(backButton, true);
        buttonLayout.add(backButton);

        card.add(title, subtitle, content, author, date, buttonLayout);
    }

    private void renderEditForm(VerticalLayout card) {
        H2 title = new H2("Edit Post");
        title.getStyle().set("color", "#ffffff"); // Already white

        TextField titleField = new TextField("Title");
        styleField(titleField);
        TextField subtitleField = new TextField("Subtitle");
        styleField(subtitleField);
        TextArea contentField = new TextArea("Content");
        contentField.setMinHeight("200px");
        styleField(contentField);

        binder.forField(titleField)
                .withValidator(new StringLengthValidator("Title must be between 5 and 100 characters", 5, 100))
                .bind(BlogResponse::getTitle, BlogResponse::setTitle);
        binder.forField(subtitleField).bind(BlogResponse::getSubtitle, BlogResponse::setSubtitle);
        binder.forField(contentField)
                .withValidator(new StringLengthValidator("Content must be at least 50 characters", 50, 10000))
                .bind(BlogResponse::getContent, BlogResponse::setContent);

        binder.readBean(post);

        Button saveButton = new Button("Save", e -> savePost());
        styleButton(saveButton, true);

        Button cancelButton = new Button("Cancel", e -> {
            isEditing = false;
            renderPost();
        });
        styleButton(cancelButton, false);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        card.add(title, titleField, subtitleField, contentField, buttonLayout);
    }

    private void savePost() {
        BlogResponse updatedPost = new BlogResponse();
        updatedPost.setId(post.getId());
        try {
            binder.writeBean(updatedPost);
            updatedPost = apiService.updatePost(updatedPost);
            post = updatedPost;
            isEditing = false;
            Notification.show("Post updated successfully!");
            renderPost();
        } catch (ValidationException e) {
            Notification.show("Please fill in all required fields.");
        } catch (Exception e) {
            handleError("Failed to update post: " + e.getMessage(), null);
        }
    }

    private void confirmDelete() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Delete Post");
        dialog.setText("Are you sure you want to delete this post? This action cannot be undone.");
        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        // Style the dialog to match the dark theme and ensure text visibility
        dialog.getElement().executeJs(
                "this.style.setProperty('--lumo-base-color', '#2d2d2d');" +
                        "this.style.setProperty('--lumo-primary-color', '#ff6f61');" +
                        "this.style.setProperty('--lumo-primary-text-color', '#ffffff');" +
                        "this.style.setProperty('--lumo-secondary-text-color', '#d1d5db');" + // For cancel button text
                        "this.querySelector('h2').style.color = '#ffffff';" + // Header already white
                        "this.querySelector('vaadin-dialog-content').style.color = '#d1d5db';" + // Body text already light gray
                        "this.querySelector('[slot=\"cancel-button\"]').style.color = '#d1d5db';" // Cancel button text set to light gray
        );

        dialog.addConfirmListener(event -> deletePost());
        dialog.open();
    }

    private void deletePost() {
        try {
            apiService.deletePost(post.getId());
            Notification.show("Post deleted successfully!");
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (Exception e) {
            handleError("Failed to delete post: " + e.getMessage(), null);
        }
    }

    private void handleError(String message, String redirectRoute) {
        Notification.show(message);
        if (redirectRoute != null) {
            getUI().ifPresent(ui -> ui.navigate(redirectRoute));
        }
    }

    private void styleField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("background", "#3a3a3a")
                .set("border", "1px solid #4b4b4b")
                .set("padding", "10px")
                .set("margin-bottom", "15px")
                .set("color", "#ffffff"); // Input text is white for visibility

        field.getElement().executeJs(
                "this.labelElement.style.color = '#d1d5db';" + // Label is light gray for visibility
                        "this.addEventListener('focus', () => {" +
                        "  this.style.borderColor = '#4db6ac';" +
                        "  this.style.boxShadow = '0 0 5px rgba(77, 182, 172, 0.3)';" +
                        "});" +
                        "this.addEventListener('blur', () => {" +
                        "  this.style.borderColor = '#4b4b4b';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }

    private void styleButton(Button button, boolean isPrimary) {
        button.getStyle()
                .set("border-radius", "12px")
                .set("padding", "12px")
                .set("background", isPrimary ? "#4db6ac" : "transparent")
                .set("color", isPrimary ? "#ffffff" : "#d1d5db") // Secondary button text changed to light gray
                .set("border", isPrimary ? "none" : "1px solid #4db6ac") // Changed border color to teal for consistency
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#26a69a" : "rgba(77, 182, 172, 0.1)") + "';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.5)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#4db6ac" : "transparent") + "';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}