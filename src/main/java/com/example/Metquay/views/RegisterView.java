package com.example.Metquay.views;

import com.example.Metquay.dtos.AuthRequestDto;
import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("register")
@PageTitle("Register")
public class RegisterView extends VerticalLayout {

    public RegisterView(ApiService apiService) {
        if (apiService.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("posts"));
            return;
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #f0f4f8, #d9e2ec)");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.1)");

        H2 title = new H2("Register");
        title.getStyle().set("color", "#102a43");

        TextField nameField = new TextField("Name");
        styleField(nameField);

        TextField emailField = new TextField("Email");
        styleField(emailField);

        PasswordField passwordField = new PasswordField("Password");
        styleField(passwordField);

        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        styleField(confirmPasswordField);

        Button registerButton = new Button("Register");
        styleButton(registerButton, true);

        registerButton.addClickListener(event -> {
            String name = nameField.getValue();
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Notification notification = new Notification("Please fill all the fields.");
                notification.getElement().getStyle()
                        .set("background", "#4db6ac")
                        .set("color", "#ffffff")
                        .set("border-radius", "8px")
                        .set("padding", "10px");
                notification.open();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Notification notification = new Notification("Passwords do not match.");
                notification.getElement().getStyle()
                        .set("background", "#4db6ac")
                        .set("color", "#ffffff")
                        .set("border-radius", "8px")
                        .set("padding", "10px");
                notification.open();
                return;
            }

            AuthRequestDto request = new AuthRequestDto();
            request.setName(name);
            request.setEmail(email);
            request.setPassword(password);

            try {
                apiService.register(request);
                Notification notification = new Notification("Registration successful!");
                notification.getElement().getStyle()
                        .set("background", "#4db6ac")
                        .set("color", "#ffffff")
                        .set("border-radius", "8px")
                        .set("padding", "10px");
                notification.open();
                getUI().ifPresent(ui -> ui.navigate("posts"));
            } catch (Exception e) {
                Notification notification = new Notification("Registration failed: " + e.getMessage());
                notification.getElement().getStyle()
                        .set("background", "#4db6ac")
                        .set("color", "#ffffff")
                        .set("border-radius", "8px")
                        .set("padding", "10px");
                notification.open();
            }
        });

        RouterLink loginLink = new RouterLink("Already have an account? Login", LoginView.class);
        loginLink.getStyle().set("color", "#4db6ac");

        RouterLink homeLink = new RouterLink("Back to Home", LandingPageView.class);
        homeLink.getStyle().set("color", "#4db6ac");

        card.add(title, nameField, emailField, passwordField, confirmPasswordField, registerButton, loginLink, homeLink);

        add(card);
    }

    private void styleField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("background", "#f9fafb")
                .set("border", "1px solid #cbd5e1")
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
                .set("transition", "all 0.3s ease");

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