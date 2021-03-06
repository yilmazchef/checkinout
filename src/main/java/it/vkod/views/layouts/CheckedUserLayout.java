package it.vkod.views.layouts;


import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import it.vkod.models.entities.Check;

import java.util.stream.Collectors;

public class CheckedUserLayout extends HorizontalLayout {

    public CheckedUserLayout(Check check) {

        this.setAlignItems(Alignment.CENTER);

        final var user = check.getAttendee().toString();

        final var avatar = new Avatar();
        avatar.setName(user);
        avatar.setImage(check.getAttendee().getProfile());

        final var name = new Span(user);
        final var roles = new Span(check.getAttendee().getRoles().stream().map(Enum::name).collect(Collectors.joining(",")));
        roles.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        final var column = new VerticalLayout(name, roles);
        column.setPadding(false);
        column.setSpacing(false);

        this.add(avatar, column);
        this.getStyle().set("line-height", "var(--lumo-line-height-m)");

    }

}
