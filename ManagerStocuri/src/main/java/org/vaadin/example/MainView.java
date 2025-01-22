package org.vaadin.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.H1; // Import pentru titluri HTML

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {

        H1 welcomeMessage = new H1("Bine ați venit în aplicația de management de stocuri!");
        welcomeMessage.getStyle().set("text-align", "center").set("color", "#007BFF");

        setSizeFull();
        addClassName("main-view");

        Button stocuriViewButton = new Button("Manager Stocuri", e -> {
            getUI().ifPresent(ui -> ui.navigate("stocuri"));
            Notification.show("Navigând către Manager Stocuri...", 1000, Notification.Position.MIDDLE);
        });

        Button comenziViewButton = new Button("Manager Comenzi", e -> {
            getUI().ifPresent(ui -> ui.navigate("comenzi"));
            Notification.show("Navigând către Manager Comenzi...", 1000, Notification.Position.MIDDLE);
        });

        Button facturiViewButton = new Button("Manager Facturi", e -> {
            getUI().ifPresent(ui -> ui.navigate("facturi"));
            Notification.show("Navigând către Manager Facturi...", 1000, Notification.Position.MIDDLE);
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(stocuriViewButton, comenziViewButton, facturiViewButton);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setSpacing(true);

        FormLayout formLayout = new FormLayout();
        formLayout.add(buttonLayout);
        formLayout.setWidth("100%");

        add(welcomeMessage, formLayout);

        getElement().getStyle().set("background-color", "#f0f0f0").set("padding", "20px");
    }
}