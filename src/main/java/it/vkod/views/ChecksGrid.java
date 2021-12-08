package it.vkod.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import it.vkod.data.dto.CheckDTO;

public class ChecksGrid extends Grid<CheckDTO> {

    public ChecksGrid() {

        setWidthFull();
        setHeightFull();
        addColumn(CheckDTO::getFirstName).setHeader("Voornaam").setKey("firstName");
        addColumn(CheckDTO::getLastName).setHeader("Familienaam").setKey("lastName");
        addColumn(CheckDTO::getEmail).setHeader("Email").setKey("email");
        addColumn(CheckDTO::getCheckedOn).setHeader("Gecheckt op").setKey("checked_on");
        addColumn(CheckDTO::getCheckedInAt).setHeader("Ingecheckt om").setKey("checked_in_at");
        addColumn(CheckDTO::getCheckedOutAt).setHeader("Uitgecheckt om").setKey("checked_out_at");
        addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

    }
}
