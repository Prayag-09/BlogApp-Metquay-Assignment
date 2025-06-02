package com.example.Metquay.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.server.VaadinSession;

public class CustomRouteNotFoundError extends RouteNotFoundError {

    public CustomRouteNotFoundError() {
        // Default constructor required by Vaadin
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        // Clear any previous content by removing all child components
        getElement().removeAllChildren();

        // Create the main layout container
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)");

        // Create the error card layout
        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setAlignItems(Alignment.CENTER);
        errorLayout.getStyle()
                .set("background", "#2d2d2d")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.5)");

        H2 errorTitle = new H2("Page Not Found");
        errorTitle.getStyle().set("color", "#ffffff"); // Already white

        Paragraph errorMessage = new Paragraph("The page you're looking for doesn't exist.");
        errorMessage.getStyle().set("color", "#d1d5db").set("text-align", "center"); // Already light gray

        Paragraph redirectMessage = new Paragraph("Redirecting you in 3 seconds...");
        redirectMessage.getStyle().set("color", "#d1d5db").set("text-align", "center").set("margin-top", "10px"); // Changed to light gray

        errorLayout.add(errorTitle, errorMessage, redirectMessage);
        mainLayout.add(errorLayout);

        // Add the main layout to the RouteNotFoundError component tree
        getElement().appendChild(mainLayout.getElement());

        // Check login status using VaadinSession
        Boolean isLoggedIn = (Boolean) VaadinSession.getCurrent().getAttribute("isLoggedIn");
        String redirectRoute = (isLoggedIn != null && isLoggedIn) ? "" : "login";

        // Schedule redirection using Vaadin's UI access for thread safety
        event.getUI().access(() -> {
            try {
                Thread.sleep(3000); // Wait for 3 seconds
                event.rerouteTo(redirectRoute);
            } catch (InterruptedException e) {
                event.rerouteTo(redirectRoute); // Fallback to immediate redirect
            }
        });

        return 404;
    }
}