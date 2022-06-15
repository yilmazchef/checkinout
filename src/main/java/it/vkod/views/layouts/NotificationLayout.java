package it.vkod.views.layouts;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class NotificationLayout {

	public static Notification success( final String message ) {

		final var notification = Notification.show( message, 3000, Notification.Position.TOP_CENTER );
		notification.addThemeVariants( NotificationVariant.LUMO_SUCCESS );
		return notification;
	}

	public static Notification error( final String message ) {

		final var notification = Notification.show( message, 5000, Notification.Position.BOTTOM_CENTER );
		notification.addThemeVariants( NotificationVariant.LUMO_ERROR );
		return notification;
	}

	public static Notification message( final String message ) {

		final var notification = Notification.show( message, 3000, Notification.Position.MIDDLE );
		notification.addThemeVariants( NotificationVariant.LUMO_PRIMARY );
		return notification;
	}

}
