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

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public LoginView(ApiService apiService) {
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

        H2 title = new H2("Login");
        title.getStyle().set("color", "#ffffff");

        TextField emailField = new TextField("Email");
        emailField.setValue("test@gmail.com");
        styleField(emailField);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setValue("Tpass@123");
        styleField(passwordField);

        Button loginButton = new Button("Sign In", e -> handleLogin(apiService, emailField, passwordField));
        styleButton(loginButton);

        RouterLink registerLink = new RouterLink("Register", RegisterView.class);
        registerLink.getStyle().set("color", "#d1d5db");

        RouterLink homeLink = new RouterLink("Back to Home", LandingPageView.class);
        homeLink.getStyle().set("color", "#d1d5db").set("margin-top", "10px");

        card.add(title, emailField, passwordField, loginButton, registerLink, homeLink);
        add(card);
    }

    private void handleLogin(ApiService apiService, TextField emailField, PasswordField passwordField) {
        String email = emailField.getValue().trim();
        String password = passwordField.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            Notification.show("Please fill in all fields");
            return;
        }

        AuthRequestDto request = new AuthRequestDto();
        request.setEmail(email);
        request.setPassword(password);

        try {
            apiService.login(request);
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (Exception ex) {
            Notification.show("Login failed: " + ex.getMessage());
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