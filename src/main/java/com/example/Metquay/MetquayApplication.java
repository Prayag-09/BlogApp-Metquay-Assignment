package com.example.Metquay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

@SpringBootApplication
@Theme("my-theme")
@PWA(name = "Metquay Blogs", shortName = "Metquay")
public class MetquayApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MetquayApplication.class, args);
        System.out.println("🚀 Metquay is running on http://localhost:3000");
    }
}