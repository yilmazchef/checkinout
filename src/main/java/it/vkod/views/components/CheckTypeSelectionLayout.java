package it.vkod.views.components;


import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.data.provider.DataProvider;
import it.vkod.models.entities.CheckType;
import lombok.Getter;

import static it.vkod.models.entities.CheckType.OTHER;

public class CheckTypeSelectionLayout extends VerticalLayout {

	@Getter
	private CheckType selectedType = OTHER;


	public CheckTypeSelectionLayout( Dialog dialog, CheckType defaultType, CheckType... options ) {

		this.selectedType = defaultType;

		final var headline = new H3( "Selecteer een typ van de check-evenement" );
		headline.getStyle().set( "margin", "var(--lumo-space-m) 0" )
				.set( "font-size", "1.1em" ).set( "font-weight", "bold" );

		final var typeSelect = new RadioButtonGroup< CheckType >();
		typeSelect.setItems( DataProvider.ofItems( options ) );
		typeSelect.addThemeVariants( RadioGroupVariant.LUMO_VERTICAL );

		setPadding( false );
		setMargin( false );
		setSpacing( false );

		setAlignItems( FlexComponent.Alignment.STRETCH );
		getStyle().set( "width", "300px" ).set( "max-width", "100%" );
		setAlignSelf( FlexComponent.Alignment.END );

		typeSelect.addValueChangeListener( onTypeSelect -> {
			selectedType = onTypeSelect.getValue();
			dialog.close();
		} );

		add( headline, typeSelect );


	}

}
