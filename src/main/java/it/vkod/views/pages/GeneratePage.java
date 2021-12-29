package it.vkod.views.pages;


import com.google.zxing.WriterException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.services.flow.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static it.vkod.utils.QRUtils.generateQR;

@PageTitle("Genereer QR")
@Route(value = "generate")
@AnonymousAllowed
public class GeneratePage extends VerticalLayout {

    private final UserService userService;
    private final String whatsappRedirectUrl = "https://api.whatsapp.com/send?phone=";

    public GeneratePage(UserService userService) {

        this.userService = userService;

        addClassNames("flex", "flex-col", "h-full");

        final var generateLayout = new VerticalLayout();

        final var formLayout = new FormLayout();

        final var usernameField = new TextField("Username / Email / Phone");
        usernameField.setRequired(true);
        usernameField.setRequiredIndicatorVisible(true);
        final var passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setClearButtonVisible(true);

        final var generateButton = new Button(
                "Generate",
                onClick -> {
                    final var keyword = usernameField.getValue();
                    final var oUser = this.userService.findByUsernameOrEmailOrPhone(
                            keyword.toLowerCase(), keyword, keyword.trim());

                    if (oUser.isPresent()) {
                        final var user = oUser.get();
                        try {

                            generateLayout.add(
                                    convertToImage(generateQR(user.getUsername(), 512, 512), user.getUsername()));
                            Notification.show(
                                    ("Generated a QR Code for " + user.getUsername()),
                                    8000,
                                    Notification.Position.BOTTOM_CENTER)
                                    .open();

                            final var sendWhatsAppButton = new Button("Verzenden via Whatsapp", onSendClick -> {
                                UI.getCurrent().getPage().setLocation(whatsappRedirectUrl.concat(user.getPhone()));
                            });

                            generateLayout.add(sendWhatsAppButton);

                        } catch (WriterException | IOException fileEx) {
                            Notification.show(fileEx.getMessage(), 3000, Notification.Position.BOTTOM_CENTER).open();
                        }
                    } else {
                        Notification.show("USER DOES NOT EXIST !! ", 3000, Notification.Position.BOTTOM_CENTER).open();
                    }
                });

        formLayout.addFormItem(usernameField, "Gebruikersnaam");
        formLayout.addFormItem(passwordField, "Wachtwoord");

        final var confirmCheck = new Checkbox(
                "I confirm that the event organizer can change my password in this session.");
        formLayout.addFormItem(confirmCheck, "Aanvaardan");

        generateLayout.setMargin(false);
        generateLayout.setPadding(false);
        generateLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        generateLayout.setAlignItems(Alignment.CENTER);
        generateLayout.add(formLayout, generateButton);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        add(generateLayout);
    }

    private Image convertToImage(final byte[] imageData, final String username) {

        return new Image(new StreamResource(
                username.concat("_QR.png"),
                (InputStreamFactory) () -> new ByteArrayInputStream(imageData)),
                username);
    }
}
