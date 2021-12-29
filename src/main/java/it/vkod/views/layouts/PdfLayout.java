package it.vkod.views.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.server.StreamResource;

@Tag("object")
public class PdfLayout extends Component implements HasSize {

    public PdfLayout(StreamResource resource) {
        this();
        getElement().setAttribute("data", resource);
    }

    public PdfLayout(String url) {
        this();
        getElement().setAttribute("data", url);
    }

    protected PdfLayout() {
        getElement().setAttribute("type", "application/pdf");
        setSizeFull();
    }
}