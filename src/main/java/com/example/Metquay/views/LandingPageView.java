package com.example.Metquay.views;

import com.example.Metquay.services.ApiService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Metquay Blogs")
public class LandingPageView extends VerticalLayout {

    public LandingPageView(ApiService apiService) {
        if (apiService.isLoggedIn()) {
            getUI().ifPresent(ui -> ui.navigate("posts"));
            return;
        }

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background", "linear-gradient(135deg, #1a1a1a, #2c2c2c)");

        FlexLayout header = createHeader();
        header.setWidthFull();
        header.getStyle().set("padding", "20px").set("background", "rgba(0, 0, 0, 0.3)");

        VerticalLayout heroSection = new VerticalLayout();
        heroSection.setAlignItems(Alignment.CENTER);
        heroSection.setJustifyContentMode(JustifyContentMode.CENTER);
        heroSection.setSpacing(false);
        heroSection.setPadding(false);

        H1 heroTitle = new H1("Share Your Stories");
        heroTitle.getStyle().set("color", "#ffffff").set("font-size", "3rem").set("text-align", "center"); // Already white

        Paragraph heroSubtitle = new Paragraph("Join our community of writers and readers.");
        heroSubtitle.getStyle().set("color", "#d1d5db").set("font-size", "1.2rem").set("text-align", "center"); // Already light gray

        Button getStartedBtn = new Button("Get Started", e -> getUI().ifPresent(ui -> ui.navigate("register")));
        styleButton(getStartedBtn, true);
        getStartedBtn.getStyle().set("margin-top", "20px");

        heroSection.add(heroTitle, heroSubtitle, getStartedBtn);

        add(header, heroSection);
        expand(heroSection);
    }

    private FlexLayout createHeader() {
        H1 logo = new H1("Metquay Blogs");
        logo.getStyle().set("color", "#ffffff").set("margin", "0").set("font-size", "1.5rem"); // Already white

        Button loginBtn = new Button("Login", e -> getUI().ifPresent(ui -> ui.navigate("login")));
        styleButton(loginBtn, false);

        Button signupBtn = new Button("Sign Up", e -> getUI().ifPresent(ui -> ui.navigate(" pukregister")));
        styleButton(signupBtn, true);

        HorizontalLayout buttonGroup = new HorizontalLayout(loginBtn, signupBtn);
        buttonGroup.setSpacing(true);
        buttonGroup.setAlignItems(Alignment.CENTER);

        FlexLayout header = new FlexLayout();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setWidthFull();

        header.add(logo, buttonGroup);
        return header;
    }

    private void styleButton(Button button, boolean isPrimary) {
        button.getStyle()
                .set("border-radius", "12px")
                .set("padding", "10px 20px")
                .set("color", isPrimary ? "#ffffff" : "#d1d5db") // Changed secondary button to light gray
                .set("background", isPrimary ? "#4db6ac" : "transparent")
                .set("border", isPrimary ? "none" : "1px solid #4db6ac")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");

        button.getElement().executeJs(
                "this.addEventListener('mouseover', () => {" +
                        "  this.style.transform = 'translateY(-2px)';" +
                        "  this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.5)';" +
                        "});" +
                        "this.addEventListener('mouseout', () => {" +
                        "  this.style.transform = 'translateY(0)';" +
                        "  this.style.boxShadow = 'none';" +
                        "});"
        );
    }
}