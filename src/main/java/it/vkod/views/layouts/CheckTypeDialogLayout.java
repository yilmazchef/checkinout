package it.vkod.views.layouts;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import it.vkod.models.entities.CheckType;

import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

public class CheckTypeDialogLayout extends Dialog {

    private final VerticalLayout layout;
    private final Select<CheckType> types;
    private final Select<String> courses;

    public CheckTypeDialogLayout(String course, CheckType... options) {

        types = (options.length > 0) ? new Select<>(options) : new Select<>(CheckType.OTHER);
        courses = new Select<>(course);
        layout = new VerticalLayout();

        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        layout.add(types, courses);

        add(layout);

    }

    public CheckTypeDialogLayout(CheckType... options) {

        types = (options.length > 0) ? new Select<>(options) : new Select<>(CheckType.OTHER);
        courses = new Select<>("JavaJun21", "JavaSept21", "PythonJan22");
        layout = new VerticalLayout();

        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setAlignItems(Alignment.CENTER);

        layout.add(types, courses);

        add(layout);

    }

    public CheckType selected() {
        return types.getValue();
    }
}
