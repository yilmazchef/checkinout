package it.vkod.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import it.vkod.data.dto.CheckDTO;
import it.vkod.services.CheckService;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ChecksGrid extends Grid<CheckDTO> {

    public ChecksGrid(final CheckService service) {

        setWidthFull();
        setHeightFull();

        addColumn(CheckDTO::getFirstName).setHeader("Voornaam").setKey("firstName");
        addColumn(CheckDTO::getLastName).setHeader("Familienaam").setKey("lastName");
        addColumn(CheckDTO::getEmail).setHeader("Email").setKey("email");
        addColumn(new LocalDateRenderer<>(CheckDTO::getCheckedOn,
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Gecheckt op").setKey("checked_on");
        addColumn(CheckDTO::getCheckedInAt).setHeader("Ingecheckt om").setKey("checked_in_at");
        addColumn(CheckDTO::getCheckedOutAt).setHeader("Uitgecheckt om").setKey("checked_out_at");
        addColumn(new ComponentRenderer<>(checkDTO -> {

            // text field for entering a new name for the person

            TextField name = new TextField("Course");
            name.setValue(checkDTO.getCourse());

            // button for saving the name to backend
            Button editButton = new Button("Edit", event -> {
                checkDTO.setCourse(name.getValue());
                getDataProvider().refreshItem(checkDTO);
            });

            // button that removes the item
            Button remove = new Button("Verwijder", event -> {
                ListDataProvider<CheckDTO> dataProvider = (ListDataProvider<CheckDTO>) getDataProvider();
                dataProvider.getItems().remove(checkDTO);
                dataProvider.refreshAll();
            });

            // layouts for placing the text field on top
            // of the buttons
            HorizontalLayout buttons = new HorizontalLayout(editButton, remove);
            return new VerticalLayout(name, buttons);
        })).setHeader("Edit");

        addColumn(new NativeButtonRenderer<>(
                "Verwijder", clickedItem -> service.deleteCheckById(clickedItem.getCheckId()))
        );

        addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        setItems(service.findCheckDetailsOfToday());

    }
}
