package it.vkod;


import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@PWA(
        name = "checkinout", shortName = "checkinout",
        iconPath = "images/logo.png",
        offlinePath = "offline.html",
        offlineResources = {"./images/offline.png"})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
