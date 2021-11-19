package it.vkod.views;


import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.vkod.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle( "Register" )
@Route( "reg" )
@AnonymousAllowed
public class RegisterView extends VerticalLayout {


	public RegisterView( @Autowired UserRepository userRepository ) {

		FormLayout layoutWithFormItems = new FormLayout();

		TextField firstName = new TextField();
		firstName.setPlaceholder( "John" );

		TextField lastName = new TextField();
		lastName.setPlaceholder( "Doe" );

		TextField phone = new TextField();
		TextField email = new TextField();
		DatePicker birthDate = new DatePicker();
		Checkbox doNotCall = new Checkbox( "Do not call" );

		layoutWithFormItems.addFormItem( firstName, "First name" );
		layoutWithFormItems.addFormItem( lastName, "Last name" );

		layoutWithFormItems.addFormItem( birthDate, "Birthdate" );
		layoutWithFormItems.addFormItem( email, "E-mail" );
		FormLayout.FormItem phoneItem = layoutWithFormItems.addFormItem( phone, "Phone" );
		phoneItem.add( doNotCall );

		add( layoutWithFormItems );

	}

}