package com.example.Metquay.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import com.vaadin.flow.server.VaadinSession;

public class CustomRouteNotFoundError extends RouteNotFoundError {

    public CustomRouteNotFoundError() {
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {

        getElement().removeAllChildren();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainLayout.getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)");

        VerticalLayout errorLayout = new VerticalLayout();
        errorLayout.setAlignItems(Alignment.CENTER);
        errorLayout.getStyle()
                .set("background", "#2d2d2d")
                .set("border-radius", "12px")
                .set("padding", "30px")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.5)");

        H2 errorTitle = new H2("Page Not Found");
        errorTitle.getStyle().set("color", "#ffffff");

        Paragraph errorMessage = new Paragraph("The page you're looking for doesn't exist.");
        errorMessage.getStyle().set("color", "#d1d5db").set("text-align", "center");

        Paragraph redirectMessage = new Paragraph("Redirecting you in 3 seconds...");
        redirectMessage.getStyle().set("color", "#d1d5db").set("text-align", "center").set("margin-top", "10px");

        errorLayout.add(errorTitle, errorMessage, redirectMessage);
        mainLayout.add(errorLayout);

        getElement().appendChild(mainLayout.getElement());

        Boolean isLoggedIn = (Boolean) VaadinSession.getCurrent().getAttribute("isLoggedIn");
        String redirectRoute = (isLoggedIn != null && isLoggedIn) ? "" : "login";

        event.getUI().access(() -> {
            try {
                Thread.sleep(3000);
                event.rerouteTo(redirectRoute);
            } catch (InterruptedException e) {
                event.rerouteTo(redirectRoute);
            }
        });

        return 404;
    }
}