package org.vaadin.example;

import jakarta.persistence.Query;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.Route;
import org.classes.ManagerStocuri;
import org.classes.Produs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.util.List;

@Route("stocuri")
public class StocuriView extends VerticalLayout {
    private ManagerStocuri managerStocuri;
    private Grid<Produs> grid;
    private EntityManager em;

    private TextField nameField = new TextField("Nume produs");
    private TextField descriptionField = new TextField("Descriere");
    private TextField priceField = new TextField("Preț");
    private IntegerField quantityField = new IntegerField("Cantitate în stoc");
    private Button saveButton = new Button("Salvează produs");
    private Button deleteButton = new Button("Șterge produs");

    public StocuriView() {

        em = Persistence.createEntityManagerFactory("myJpaUnit").createEntityManager();
        List<Produs> produse = em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        managerStocuri = new ManagerStocuri(produse);

        grid = new Grid<>(Produs.class);
        grid.setItems(managerStocuri.getProduse());
        grid.setColumns("nume", "descriere", "pret", "cantitateInStoc");
        grid.asSingleSelect().addValueChangeListener(event -> selectProdus(event.getValue()));

        add(grid, createFormLayout());
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();

        saveButton.addClickListener(e -> saveProdus());

        deleteButton.addClickListener(e -> deleteProdus());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, deleteButton);
        formLayout.add(nameField, descriptionField, priceField, quantityField, buttonsLayout);

        return formLayout;
    }

    private void saveProdus() {
        String name = nameField.getValue();
        String description = descriptionField.getValue();
        String priceValue = priceField.getValue();
        Integer quantity = quantityField.getValue();

        if (name.isEmpty() || description.isEmpty() || priceValue.isEmpty() || quantity == null) {
            Notification.show("Toate câmpurile trebuie să fie completate!");
            return;
        }

        try {
            double price = Double.parseDouble(priceValue);

            Produs selectedProdus = grid.asSingleSelect().getValue();
            em.getTransaction().begin();

            if (selectedProdus == null) {

                Produs newProdus = new Produs();
                newProdus.setNume(name);
                newProdus.setDescriere(description);
                newProdus.setPret(price);
                newProdus.setCantitateInStoc(quantity != null ? quantity : 0);
                em.persist(newProdus);
                managerStocuri.adaugaProdus(newProdus);
                Notification.show("Produs adăugat cu succes.");
            } else {

                selectedProdus.setNume(name);
                selectedProdus.setDescriere(description);
                selectedProdus.setPret(price);
                selectedProdus.setCantitateInStoc(quantity != null ? quantity : 0);
                em.merge(selectedProdus);
                Notification.show("Produs actualizat cu succes.");
            }

            em.getTransaction().commit();
            updateGrid();
            clearForm();
        } catch (NumberFormatException ex) {
            Notification.show("Prețul trebuie să fie un număr valid.");
        }
    }

    private void deleteProdus() {

        Produs selectedProdus = grid.asSingleSelect().getValue();

        if (selectedProdus != null) {
            try {

                em.getTransaction().begin();


                String sql = "DELETE FROM comanda_produs WHERE produse_nume = ?";
                Query query = em.createNativeQuery(sql);
                query.setParameter(1, selectedProdus.getNume());
                query.executeUpdate();


                if (!em.contains(selectedProdus)) {
                    selectedProdus = em.merge(selectedProdus);
                }


                em.remove(selectedProdus);

                em.getTransaction().commit();

                updateGrid();

                clearForm();

                Notification.show("Produs șters cu succes.");
            } catch (Exception e) {

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                Notification.show("Eroare la ștergerea produsului: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        } else {

            Notification.show("Selectați un produs pentru a-l șterge.");
        }
    }


    private void selectProdus(Produs produs) {
        if (produs != null) {
            nameField.setValue(produs.getNume() != null ? produs.getNume() : "");
            descriptionField.setValue(produs.getDescriere() != null ? produs.getDescriere() : "");
            priceField.setValue(String.valueOf(produs.getPret()));
            quantityField.setValue(produs.getCantitateInStoc());
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        quantityField.clear();
        grid.asSingleSelect().clear();
    }

    private void updateGrid() {

        List<Produs> produse = em.createQuery("SELECT p FROM Produs p", Produs.class).getResultList();
        grid.setItems(produse);
    }
}