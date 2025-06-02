package com.example.Metquay.views;

import com.example.Metquay.dtos.BlogResponse;
import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "create-post", layout = MainLayout.class)
@PageTitle("Create Post")
public class CreatePostView extends VerticalLayout {

    private final ApiService apiService;
    private final Binder<BlogResponse> binder;

    public CreatePostView(ApiService apiService) {
        this.apiService = apiService;
        this.binder = new Binder<>(BlogResponse.class);

        if (!apiService.isLoggedIn()) {
            handleError("Unauthorized access. Please log in.", "login");
            return;
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "#f4f4f5").set("padding", "20px");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("600px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        H2 title = new H2("Create New Post");
        title.getStyle().set("color", "#333333");

        TextField titleField = new TextField("Title");
        styleField(titleField);
        TextField subtitleField = new TextField("Subtitle");
        styleField(subtitleField);
        TextArea contentField = new TextArea("Content");
        contentField.setMinHeight("200px");
        styleField(contentField);

        Button submitButton = new Button("Publish", e -> publishPost());
        styleButton(submitButton, true);

        binder.forField(titleField)
                .withValidator(new StringLengthValidator("Title must be between 5 and 100 characters", 5, 100))
                .bind(BlogResponse::getTitle, BlogResponse::setTitle);
        binder.forField(subtitleField).bind(BlogResponse::getSubtitle, BlogResponse::setSubtitle);
        binder.forField(contentField)
                .withValidator(new StringLengthValidator("Content must be at least 50 characters", 50, 10000))
                .bind(BlogResponse::getContent, BlogResponse::setContent);

        card.add(title, titleField, subtitleField, contentField, submitButton);
        add(card);
    }

    private void publishPost() {
        BlogResponse post = new BlogResponse();
        try {
            binder.writeBean(post);
            apiService.createPost(post);
            Notification notification = new Notification("Post published successfully!");
            notification.getElement().getStyle()
                    .set("background", "#007bff")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (ValidationException e) {
            Notification notification = new Notification("Please fill in all required fields.");
            notification.getElement().getStyle()
                    .set("background", "#007bff")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
        } catch (Exception e) {
            handleError("Failed to publish post: " + e.getMessage(), null);
        }
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

    private void styleField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("border", "1px solid #d1d5db")
                .set("padding", "10px")
                .set("margin-bottom", "15px")
                .set("color", "#333333");

        field.getElement().executeJs(
                "this.labelElement.style.color = '#666666';" +
                        "this.addEventListener('focus', () => {" +
                        "  this.style.borderColor = '#007bff';" +
                        "  this.style.boxShadow = '0 0 5px rgba(0,123,255,0.3)';" +
                        "});" +
                        "this.addEventListener('blur', () => {" +
                        "  this.style.borderColor = '#d1d5db';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }

    private void styleButton(Button button, boolean isPrimary) {
        button.getStyle()
                .set("width", "100%")
                .set("border-radius", "12px")
                .set("padding", "12px")
                .set("background", isPrimary ? "#007bff" : "#ffffff")
                .set("color", isPrimary ? "#ffffff" : "#333333")
                .set("border", isPrimary ? "none" : "1px solid #d1d5db")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#0056b3" : "#e5e7eb") + "';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '" + (isPrimary ? "#007bff" : "#ffffff") + "';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}