package com.example.Metquay.views;

import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final ApiService apiService;
    private Button logoutButton;
    private RouterLink createPostLink;
    private RouterLink postsLink;

    public MainLayout(ApiService apiService) {
        this.apiService = apiService;
        createHeader();
        createDrawer();

        setDrawerStyle();
    }

    private void createHeader() {
        H1 logo = new H1("Metquay Blogs");
        logo.getStyle().set("color", "#ffffff").set("font-size", "1.5rem");

        logoutButton = new Button("Logout", e -> handleLogout());
        logoutButton.getStyle()
                .set("border-radius", "12px")
                .set("padding", "8px 16px")
                .set("color", "#ffffff") // Changed to white for better visibility
                .set("background", "transparent")
                .set("border", "1px solid #4db6ac")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        logoutButton.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = 'rgba(77, 182, 172, 0.2)';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.5)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = 'transparent';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );

        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "#ffffff");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, logoutButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("padding", "0 20px");
        addToNavbar(header);
    }

    private void createDrawer() {
        postsLink = new RouterLink("Posts", PostsView.class);
        styleNavLink(postsLink);
        createPostLink = new RouterLink("Create Post", CreatePostView.class);
        styleNavLink(createPostLink);

        VerticalLayout drawer = new VerticalLayout(postsLink, createPostLink);
        drawer.getStyle().set("padding", "20px").set("background", "transparent");
        addToDrawer(drawer);
    }

    private void handleLogout() {
        try {
            apiService.logout();
            VaadinSession.getCurrent().getSession().invalidate();
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (Exception e) {
            Notification.show("Logout failed: " + e.getMessage());
        }
    }

    private void styleNavLink(RouterLink link) {
        link.getStyle()
                .set("color", "#d1d5db")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("text-decoration", "none")
                .set("display", "block")
                .set("background", "transparent");

        link.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#4db6ac';" +
                        "  this.style.color = '#ffffff';" +
                        "  this.style.transform = 'translateX(5px)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = 'transparent';" +
                        "  this.style.color = '#d1d5db';" +
                        "  this.style.transform = 'translateX(0)';" +
                        "});"
        );
    }

    private void setDrawerStyle() {
        getElement().executeJs(
                "const drawer = this.shadowRoot.querySelector('[part=\"drawer\"]');" +
                        "if (drawer) {" +
                        "  drawer.style.background = '#2d2d2d';" +
                        "  drawer.querySelectorAll('*').forEach(el => el.style.background = 'transparent');" +
                        "}" +
                        "const navbar = this.shadowRoot.querySelector('[part=\"navbar\"]');" +
                        "if (navbar) {" +
                        "  navbar.style.background = 'linear-gradient(135deg, #1a1a1a, #2c2c2c)';" +
                        "}"
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean loggedIn = apiService.isLoggedIn();
        logoutButton.setVisible(loggedIn);
        createPostLink.setVisible(loggedIn);
        postsLink.setVisible(loggedIn);

        if (!loggedIn && !event.getNavigationTarget().equals(LoginView.class) && !event.getNavigationTarget().equals(RegisterView.class)) {
            event.rerouteTo("login");
        }
    }
}