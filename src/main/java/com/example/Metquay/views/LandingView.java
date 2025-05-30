package com.example.Metquay.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("padding", "2rem");

        H1 heading = new H1("Welcome to Metquay Blog");
        heading.getStyle()
                .set("font-size", "2.5rem")
                .set("margin-bottom", "1rem")
                .set("text-align", "center");

        Paragraph description = new Paragraph("Discover amazing blog posts and share your thoughts with the world.");
        description.getStyle()
                .set("font-size", "1.2rem")
                .set("margin-bottom", "2rem")
                .set("text-align", "center")
                .set("color", "var(--lumo-secondary-text-color)");

        // Check if user is authenticated
        boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser");

        if (isAuthenticated) {
            Button createButton = new Button("Create a Blog Post", e ->
                    UI.getCurrent().navigate("create"));
            createButton.addThemeName("primary");
            createButton.getStyle()
                    .set("font-size", "1.1rem")
                    .set("padding", "0.6rem 2rem");

            Button viewBlogsButton = new Button("View All Blogs", e ->
                    UI.getCurrent().navigate("blogs"));
            viewBlogsButton.addThemeName("tertiary");
            viewBlogsButton.getStyle()
                    .set("font-size", "1.1rem")
                    .set("padding", "0.6rem 2rem")
                    .set("margin-left", "1rem");

            add(heading, description, createButton, viewBlogsButton);
        } else {
            Button loginButton = new Button("Login to Get Started", e ->
                    UI.getCurrent().navigate("login"));
            loginButton.addThemeName("primary");
            loginButton.getStyle()
                    .set("font-size", "1.1rem")
                    .set("padding", "0.6rem 2rem");

            Button viewBlogsButton = new Button("Browse Blogs", e ->
                    UI.getCurrent().navigate("blogs"));
            viewBlogsButton.addThemeName("tertiary");
            viewBlogsButton.getStyle()
                    .set("font-size", "1.1rem")
                    .set("padding", "0.6rem 2rem")
                    .set("margin-left", "1rem");

            add(heading, description, loginButton, viewBlogsButton);
        }

        // Show logout success notification
        List<String> logoutParam = UI.getCurrent().getInternals()
                .getActiveViewLocation()
                .getQueryParameters()
                .getParameters()
                .get("logout");

        if (logoutParam != null && !logoutParam.isEmpty()) {
            Notification.show("You have been logged out successfully.", 3000, Notification.Position.TOP_CENTER);
        }
    }
}