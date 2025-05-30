package com.example.Metquay.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        H1 logo = new H1("Metquay Blog");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        RouterLink homeLink = new RouterLink("Home", LandingView.class);
        RouterLink blogLink = new RouterLink("Blogs", BlogsListView.class);

        HorizontalLayout navigation = new HorizontalLayout(homeLink, blogLink);
        navigation.setSpacing(true);

        // Add login/logout button based on authentication status
        HorizontalLayout authSection = new HorizontalLayout();

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {

            Button logoutButton = new Button("Logout", e -> logout());
            logoutButton.addThemeName("tertiary");
            authSection.add(logoutButton);
        } else {
            RouterLink loginLink = new RouterLink("Login", LoginView.class);
            authSection.add(loginLink);
        }

        HorizontalLayout leftSection = new HorizontalLayout(logo, navigation);
        leftSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);

        HorizontalLayout header = new HorizontalLayout(leftSection, authSection);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle().set("padding", "var(--lumo-space-m)");

        addToNavbar(header);
    }

    private void logout() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
        getUI().ifPresent(ui -> ui.navigate(""));
    }
}