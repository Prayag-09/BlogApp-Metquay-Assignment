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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

/**
 * @author prayagtushar
 */
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
        getStyle().set("background", "#f4f4f5"); // Light gray background

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background", "#ffffff") // White card
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)"); // Lighter shadow

        H2 title = new H2("Login");
        title.getStyle().set("color", "#333333"); // Dark text

        TextField emailField = new TextField("Email");
        styleField(emailField);

        PasswordField passwordField = new PasswordField("Password");
        styleField(passwordField);

        Button loginButton = new Button("Sign In", e -> handleLogin(apiService, emailField, passwordField));
        styleButton(loginButton);

        RouterLink registerLink = new RouterLink("Register", RegisterView.class);
        registerLink.getStyle().set("color", "#007bff"); // Blue link

        RouterLink homeLink = new RouterLink("Back to Home", LandingPageView.class);
        homeLink.getStyle().set("color", "#007bff").set("margin-top", "10px");

        card.add(title, emailField, passwordField, loginButton, registerLink, homeLink);
        add(card);
    }

    private void handleLogin(ApiService apiService, TextField emailField, PasswordField passwordField) {
        String email = emailField.getValue().trim();
        String password = passwordField.getValue();

        if (email.isEmpty() || password.isEmpty()) {
            Notification notification = new Notification("Please fill in all fields");
            notification.getElement().getStyle()
                    .set("background", "#007bff")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
            return;
        }

        AuthRequestDto request = new AuthRequestDto();
        request.setEmail(email);
        request.setPassword(password);

        try {
            apiService.login(request);
            getUI().ifPresent(ui -> ui.navigate("posts"));
        } catch (Exception ex) {
            Notification notification = new Notification("Login failed: " + ex.getMessage());
            notification.getElement().getStyle()
                    .set("background", "#007bff")
                    .set("color", "#ffffff")
                    .set("border-radius", "8px")
                    .set("padding", "10px");
            notification.open();
        }
    }

    private void styleField(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("background", "#ffffff") // White input background
                .set("border", "1px solid #d1d5db") // Light gray border
                .set("padding", "10px")
                .set("margin-bottom", "15px")
                .set("color", "#333333"); // Dark text

        field.getElement().executeJs(
                "this.labelElement.style.color = '#333333';" +
                        "this.addEventListener('focus', () => {" +
                        "  this.style.borderColor = '#007bff';" + // Blue border on focus
                        "  this.style.boxShadow = '0 0 5px rgba(0,123,255,0.3);" +
                        "});" +
                        "this.addEventListener('blur', () => {" +
                        "  this.style.borderColor = '#d1d5db';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }

    private void styleButton(Button button) {
        button.getStyle()
                .set("width", "100%")
                .set("border-radius", "8px")
                .set("padding", "12px")
                .set("background", "#007bff") // Blue button
                .set("color", "#ffffff")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#0056b3';" + // Darker blue on hover
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