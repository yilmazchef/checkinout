package it.vkod.views.pwa;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Checkinout Home")
@Route(value = "", layout = AppLayoutBottomNavbar.class)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    public HomeView() {

        setAlignItems(Alignment.CENTER);

        add(new Text("Welcome bij het aanmeldingssysteem van Intec Brussel "));

        add(new Image("images/intec-logo.png", "Intec Brussel Logo"));

    }
}
