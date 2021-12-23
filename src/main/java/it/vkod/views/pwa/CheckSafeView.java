package it.vkod.views.pwa;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

import javax.annotation.security.RolesAllowed;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.elmot.flow.sensors.GeoLocation;

import it.vkod.models.entities.Check;
import it.vkod.models.entities.CheckType;
import it.vkod.services.flow.AuthenticationService;
import it.vkod.services.flow.CheckService;
import it.vkod.services.flow.UserService;
import it.vkod.views.components.CheckedUserLayout;
import it.vkod.views.components.NotificationUtils;

@PageTitle("Inchecken/Uitchecken")
@Route(value = "m/safe", layout = MobileLayout.class)
@RouteAlias(value = "mobile/failsafe", layout = MobileLayout.class)
@RolesAllowed({ "ADMIN", "MANAGER", "LEADER" })
public class CheckSafeView extends VerticalLayout {

	public CheckSafeView(@Autowired AuthenticationService authService, @Autowired UserService userService,
			@Autowired CheckService checkService) {

		authService.get().ifPresent(organizer -> {

			final var location = new GeoLocation();
			location.setWatch(true);
			location.setHighAccuracy(true);
			location.setTimeout(100000);
			location.setMaxAge(200000);
			add(location);

			final var failSafeForm = new FormLayout();

			final var dateTimePicker = new DateTimePicker();
			dateTimePicker.setLabel("Het datum van de check");
			dateTimePicker.setHelperText("Het datum moet tussen de laatste 60 dagen zijn.");
			dateTimePicker.setAutoOpen(true);
			dateTimePicker.setMin(LocalDateTime.now().minusDays(1));
			dateTimePicker.setMax(LocalDateTime.now().plusDays(7));
			dateTimePicker.setValue(LocalDateTime.now());

			final var username = new TextField("Gebruikersnaam:");
			final var checks = checkService.fromCourse(organizer.getCourse());

			final var checkButton = new Button(VaadinIcon.SIGN_IN_ALT.create());
			checkButton.addClickListener(onCheckIn -> {
				final var check = checkService.create(new Check()
						.setOrganizer(organizer)
						.setAttendee(userService.getByUsername(username.getValue()))
						.setActive(true)
						.setCheckedOn(Date.valueOf(LocalDate.now()))
						.setCheckedInAt(Time.valueOf(LocalTime.now()))
						.setCheckedOutAt(Time.valueOf(LocalTime.now()))
						.setCourse(organizer.getCourse())
						.setLat(location.getValue().getLatitude())
						.setLon(location.getValue().getLongitude())
						.setPin(new Random().nextInt(8999) + 1000)
						.setSession(VaadinSession.getCurrent().getSession().getId())
						.setValidLocation(true)
						.setType(CheckType.PHYSICAL_IN));

				checks.add(check);

				final var checkLayout = new CheckedUserLayout(check);
				add(checkLayout);

				NotificationUtils.success(
						check.getAttendee().getFirstName()
								+ " "
								+ check.getAttendee().getLastName()
								+ (check.getType() == CheckType.PHYSICAL_IN ? " heeft ingecheckt "
										: " heeft uitgecheckt")
								+ ".")
						.open();
			});

			failSafeForm.add(username, dateTimePicker, checkButton);
			add(failSafeForm);

		});

	}

}
