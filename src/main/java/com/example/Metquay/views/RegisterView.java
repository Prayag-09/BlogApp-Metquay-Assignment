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
        getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "#2d2d2d")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.5)");

        H2 title = new H2("Register");
        title.getStyle().set("color", "#ffffff");

        TextField nameField = new TextField("Name");
        styleField(nameField);

        TextField emailField = new TextField("Email");
        styleField(emailField);

        PasswordField passwordField = new PasswordField("Password");
        styleField(passwordField);

        Button registerButton = new Button("Create Account", e -> handleRegister(apiService, nameField, emailField, passwordField));
        styleButton(registerButton);

        RouterLink loginLink = new RouterLink("Login", LoginView.class);
        loginLink.getStyle().set("color", "#d1d5db");

        RouterLink homeLink = new RouterLink("Back to Home", LandingPageView.class);
        homeLink.getStyle().set("color", "#d1d5db").set("margin-top", "10px");

        card.add(title, nameField, emailField, passwordField, registerButton, loginLink, homeLink);
        add(card);
    }

    private void handleRegister(ApiService apiService, TextField nameField, TextField emailField, PasswordField passwordField) {
        String name = nameField.getValue().trim();
        String email = emailField.getValue().trim();
        String password = passwordField.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Notification.show("Please fill in all fields");
            return;
        }

        if (name.length() < 2) {
            Notification.show("Name must be at least 2 characters long");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            Notification.show("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            Notification.show("Password must be at least 6 characters long");
            return;
        }

        AuthRequestDto request = new AuthRequestDto();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);

        try {
            apiService.register(request);
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (Exception ex) {
            handleError("Registration failed: " + ex.getMessage(), null);
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
                .set("color", "#ffffff");

        field.getElement().executeJs(
                "this.labelElement.style.color = '#d1d5db';" +
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

    private void styleButton(Button button) {
        button.getStyle()
                .set("width", "100%")
                .set("border-radius", "12px")
                .set("padding", "12px")
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