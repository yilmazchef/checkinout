package it.vkod.views.desktop;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.wontlost.zxing.Constants;
import com.wontlost.zxing.ZXingVaadinReader;

import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.UserService;

@PageTitle("Valideer QR")
@Route(value = "val", layout = DesktopAppLayout.class)
@RouteAlias(value = "validate", layout = DesktopAppLayout.class)
@PermitAll
public class DesktopValidateView extends VerticalLayout {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public DesktopValidateView(AuthenticationService authenticationService, UserService userService) {

        this.authenticationService = authenticationService;
        this.userService = userService;

        initStyle();

        final var user = this.authenticationService.get();

        if (user.isPresent()) {

            final var scanLayout = new VerticalLayout();
            final var reader = new ZXingVaadinReader();

            reader.setFrom(Constants.From.camera);
            reader.setId("video"); // id needs to be 'video' if From.camera.
            reader.setStyle("object-fit: cover; width:100; height:100vh; max-height:100");

            reader.addValueChangeListener(scannedQRCode -> validateUser(scannedQRCode.getValue()));

            scanLayout.setMargin(false);
            scanLayout.setPadding(false);
            scanLayout.setJustifyContentMode(JustifyContentMode.CENTER);
            scanLayout.setAlignItems(Alignment.CENTER);
            scanLayout.add(reader);

            add(scanLayout);
        }

    }

    private void validateUser(final String scannedQRCode) {

        final var oAttendee = this.userService.findByUsername(scannedQRCode);
        if (oAttendee.isPresent()) {
            Notification.show(("QR-code is valid!"), 4000, Notification.Position.BOTTOM_CENTER).open();
        } else {
            Notification.show(("QR-code is GEEN valid: " + scannedQRCode), 4000, Notification.Position.BOTTOM_CENTER)
                    .open();
        }
    }

    private void initStyle() {

        addClassNames("flex", "flex-col", "h-full");
        setMargin(false);
        setPadding(false);
        setSpacing(false);
        setWidthFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
    }

}