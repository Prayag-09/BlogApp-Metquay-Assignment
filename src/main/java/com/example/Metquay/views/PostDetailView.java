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
        getStyle().set("background", "linear-gradient(135deg, #f0f4f8, #d9e2ec)").set("padding", "20px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!apiService.isLoggedIn()) {
            handleError("Unauthorized access. Please log in.", "posts");
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
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 5px 20px rgba(0,0,0,0.1)");

        if (isEditing) {
            renderEditForm(card);
        } else {
            renderViewMode(card);
        }

        add(card);
    }

    private void renderViewMode(VerticalLayout card) {
        H2 title = new H2(post.getTitle());
        title.getStyle().set("color", "#102a43");

        Paragraph subtitle = new Paragraph(post.getSubtitle() != null ? post.getSubtitle() : "");
        subtitle.getStyle().set("color", "#486581").set("font-size", "1.1rem");

        Paragraph content = new Paragraph(post.getContent());
        content.getStyle().set("color", "#334e68").set("white-space", "pre-wrap");

        Paragraph author = new Paragraph("By " + (post.getAuthor() != null ? post.getAuthor().getName() : "Unknown"));
        author.getStyle().set("color", "#627d98").set("font-style", "italic");

        Paragraph date = new Paragraph("Published on " + (post.getCreatedAt() != null ? post.getCreatedAt().toString() : "Unknown"));
        date.getStyle().set("color", "#627d98").set("font-size", "0.9rem");

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
        title.getStyle().set("color", "#102a43");

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
            Notification notification = new Notification("Post updated successfully!");
            notification.getElement().getStyle()
                    .set("background", "#4db6ac")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
            renderPost();
        } catch (ValidationException e) {
            Notification notification = new Notification("Please fill in all required fields.");
            notification.getElement().getStyle()
                    .set("background", "#4db6ac")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
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

        dialog.getElement().executeJs(
                "this.style.setProperty('--lumo-base-color', '#ffffff');" +
                        "this.style.setProperty('--lumo-color', '#007bff');" +
                        "this.style.setProperty('--lumo-primary-text-color', '#007bff');" +
                        "this.style.setProperty('--lumo-secondary-text-color', '#333333');" +
                        "this.querySelector('h2').style.color = '#333333';" +
                        "this.querySelector('vaadin-dialog-content').style.color = '#333333';" +
                        "this.querySelector('[slot=\"cancel-button\"]').style.color = '#333333';"
        );

        dialog.addConfirmListener(event -> deletePost());
        dialog.open();
    }

    private void deletePost() {
        try {
            apiService.deletePost(post.getId());
            Notification notification = new Notification("Post deleted successfully!");
            notification.getElement().getStyle()
                    .set("background", "#4db6ac")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (Exception e) {
            handleError("Failed to delete post: " + e.getMessage(), null);
        }
    }

    private void handleError(String message, String redirectRoute) {
        Notification notification = new Notification(message);
        notification.getElement().getStyle()
                .set("background", "#4db6ac")
                .set("color", "#ffffff")
                .set("border-radius", "8px")
                .set("padding", "10px");
        notification.open();
        if (redirectRoute != null) {
            getUI().ifPresent(ui -> ui.navigate(redirectRoute));
        }
    }

    private void styleField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("background", "#f9fafb")
                .set("border", "1px solid #1d5db8")
                .set("padding", "10px")
                .set("margin-bottom", "15px")
                .set("color", "#102a43");

        field.getElement().executeJs(
                "this.labelElement.style.color = '#486581';" +
                        "this.addEventListener('focus', () => {" +
                        "  this.style.borderColor = '#4db6ac';" +
                        "  this.style.boxShadow = '0 0 5px rgba(77, 182, 172, 0.3)';" +
                        "});" +
                        "this.addEventListener('blur', () => {" +
                        "  this.style.borderColor = '#cbd5e1';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }

    private void styleButton(Button button, boolean isPrimary) {
        button.getStyle()
                .set("border-radius", "12px")
                .set("padding", "12px")
                .set("background", isPrimary ? "#4db6ac" : "transparent")
                .set("color", isPrimary ? "#ffffff" : "#486581")
                .set("border", isPrimary ? "none" : "1px solid #4db6ac")
                .set("cursor", "pointer")
                .set("transition", "all 0.3 ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#26a69a" : "rgba(77, 182, 172, 0.1)") + "';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.1)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#4db6ac" : "transparent") + "';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}
