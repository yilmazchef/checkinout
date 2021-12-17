package it.vkod.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ScrollableHorizontalLayout extends HorizontalLayout {

    private HorizontalLayout content;

    public ScrollableHorizontalLayout() {
        super();
        preparePanel();
    }

    public ScrollableHorizontalLayout(Component... children) {
        super();
        preparePanel();
        this.add(children);
    }

    private void preparePanel() {
        getStyle().set("overflow", "auto");
        content = new HorizontalLayout();
        content.getStyle().set("display", "block");
        content.setWidth("100%");
        super.add(content);
        setHeight("100%");
    }

    public HorizontalLayout getContent() {
        return content;
    }

    @Override
    public void add(Component... components) {
        content.add(components);
    }

    @Override
    public void remove(Component... components) {
        content.remove(components);
    }

    @Override
    public void removeAll() {
        content.removeAll();
    }

    @Override
    public void addComponentAsFirst(Component component) {
        content.addComponentAtIndex(0, component);
    }
}
