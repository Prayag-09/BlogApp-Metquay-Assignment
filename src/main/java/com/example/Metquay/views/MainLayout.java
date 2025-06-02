package com.example.Metquay.views;

import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author prayagtushar
 */
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
        logo.getStyle()
                .set("color", "#333333")
                .set("font-size", "1.5rem");

        logoutButton = new Button("Logout", e -> handleLogout());
        logoutButton.getStyle()
                .set("border-radius", "8px")
                .set("padding", "8px 16px")
                .set("color", "#ffffff")
                .set("background", "#007bff")
                .set("border", "none")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        logoutButton.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#0056b3';" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = '#007bff';" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );

        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "#333333");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, logoutButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("padding", "0 20px")
                .set("background", "#ffffff")
                .set("border-bottom", "1px solid #e5e7eb");
        addToNavbar(header);
    }

    private void createDrawer() {
        postsLink = new RouterLink("Posts", PostsView.class);
        styleNavLink(postsLink);
        createPostLink = new RouterLink("Create Post", CreatePostView.class);
        styleNavLink(createPostLink);

        VerticalLayout drawer = new VerticalLayout(postsLink, createPostLink);
        drawer.getStyle()
                .set("padding", "20px")
                .set("background", "#f4f4f5");
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
                .set("color", "#333333")
                .set("padding", "10px 20px")
                .set("border-radius", "8px")
                .set("text-decoration", "none")
                .set("display", "block")
                .set("background", "transparent");

        link.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.background = '#e5e7eb';" +
                        "  this.style.color = '#007bff';" +
                        "  this.style.transform = 'translateX(5px)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.background = 'transparent';" +
                        "  this.style.color = '#333333';" +
                        "  this.style.transform = 'translateX(0)';" +
                        "});"
        );
    }

    private void setDrawerStyle() {
        getElement().executeJs(
                "const drawer = this.shadowRoot.querySelector('[part=\"drawer\"]');" +
                        "if (drawer) {" +
                        "  drawer.style.background = '#f4f4f5';" +
                        "  drawer.querySelectorAll('*').forEach(el => el.style.background = 'transparent');" +
                        "}" +
                        "const navbar = this.shadowRoot.querySelector('[part=\"navbar\"]');" +
                        "if (navbar) {" +
                        "  navbar.style.background = '#ffffff';" +
                        "  navbar.style.borderBottom = '1px solid #e5e7eb';" +
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