package it.vkod.services.exceptions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import it.vkod.views.components.NotificationUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorNotificationHandler implements ErrorHandler {

    @Override
    public void error(ErrorEvent errorEvent) {
        log.error("Something wrong happened", errorEvent.getThrowable());
        if (UI.getCurrent() != null) {
            UI.getCurrent().access(() ->
                    NotificationUtils.error(errorEvent.getThrowable().getMessage()).open());
        }
    }
}