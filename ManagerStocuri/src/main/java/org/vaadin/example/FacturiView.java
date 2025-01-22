package org.vaadin.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.router.Route;
import org.classes.Factura;
import org.classes.Comanda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
@Route("facturi")
public class FacturiView extends VerticalLayout {

    private static final Logger LOGGER = Logger.getLogger(FacturiView.class.getName());  // Adăugat Logger
    private List<Factura> facturi = new ArrayList<>();
    private Grid<Factura> grid;
    private EntityManager em;

    private TextField idFacturaField = new TextField("ID Factură");
    private TextField comandaIdField = new TextField("ID Comandă");
    private TextField sumaDePlataField = new TextField("Sumă de plată");
    private DatePicker dataEmitereField = new DatePicker("Data emiterii");
    private Button saveButton = new Button("Salvează factură");
    private Button deleteButton = new Button("Șterge factură");

    public FacturiView() {
        em = Persistence.createEntityManagerFactory("myJpaUnit").createEntityManager();

        populateFacturiExistent();

        grid = new Grid<>(Factura.class);
        grid.setColumns("idFactura", "comanda.idComanda", "sumaDePlata", "dataEmitere");

        updateGrid();

        grid.asSingleSelect().addValueChangeListener(event -> selectFactura(event.getValue()));

        add(grid, createFormLayout());
    }
    private void populateFacturiExistent() {
        try {

            facturiExistent = em.createQuery(" SELECT f FROM Factura f", Factura.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Eroare la încărcarea facturilor existente.");
        }
    }


    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();

        saveButton.addClickListener(e -> saveFactura());
        deleteButton.addClickListener(e -> deleteFactura());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, deleteButton);
        formLayout.add(idFacturaField, comandaIdField, sumaDePlataField, dataEmitereField, buttonsLayout);

        return formLayout;
    }

    private void saveFactura() {
        String idFacturaStr = idFacturaField.getValue();
        String comandaIdStr = comandaIdField.getValue();
        String sumaDePlataStr = sumaDePlataField.getValue();
        LocalDate dataEmitere = dataEmitereField.getValue();

        if (comandaIdStr.isEmpty() || sumaDePlataStr.isEmpty() || dataEmitere == null) {
            Notification.show("Toate câmpurile trebuie să fie completate!");
            return;
        }

        try {

            int idFactura = idFacturaStr.isEmpty() ? 0 : Integer.parseInt(idFacturaStr);  // ID-ul facturii
            int comandaId = Integer.parseInt(comandaIdStr);  // ID-ul comenzii
            double sumaDePlata = Double.parseDouble(sumaDePlataStr);  // Suma de plată

            Comanda comanda = em.find(Comanda.class, comandaId);

            if (comanda == null) {

                comanda = new Comanda();
                comanda.setIdComanda(comandaId);
                em.persist(comanda);
                Notification.show("Comanda cu ID-ul " + comandaId + " a fost creată.");
            }


            em.getTransaction().begin();

            Factura selectedFactura = grid.asSingleSelect().getValue();
            if (selectedFactura == null) {

                Factura newFactura = new Factura();
                newFactura.setIdFactura(idFactura);
                newFactura.setComanda(comanda);
                newFactura.setSumaDePlata(sumaDePlata);
                newFactura.setDataEmitere(dataEmitere.toString());

                em.persist(newFactura);

                facturiExistent.add(newFactura);
                Notification.show("Factură adăugată cu succes.");
            } else {

                selectedFactura.setIdFactura(idFactura);
                selectedFactura.setComanda(comanda);
                selectedFactura.setSumaDePlata(sumaDePlata);
                selectedFactura.setDataEmitere(dataEmitere.toString());

                em.merge(selectedFactura);
                Notification.show("Factură actualizată cu succes.");
            }

            em.getTransaction().commit();

            updateGrid();
            clearForm();
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "Format invalid al datelor", ex);
            Notification.show("Datele introduse nu sunt valide.");
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Eroare la salvarea facturii", ex);
            Notification.show("Eroare la salvarea facturii.");
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }


    private void deleteFactura() {

        Factura selectedFactura = grid.asSingleSelect().getValue();

        if (selectedFactura != null) {
            try {

                em.getTransaction().begin();

                if (!em.contains(selectedFactura)) {
                    selectedFactura = em.merge(selectedFactura);
                }

                em.remove(selectedFactura);

                em.getTransaction().commit();

                facturi.remove(selectedFactura);
                facturiExistent.remove(selectedFactura);
                updateGrid();

                clearForm();

                Notification.show("Factură ștearsă cu succes.");
            } catch (Exception e) {

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }

                Notification.show("Eroare la ștergerea facturii: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        } else {

            Notification.show("Selectați o factură pentru a o șterge.");
        }
    }




    private void selectFactura(Factura factura) {
        if (factura != null) {
            idFacturaField.setValue(String.valueOf(factura.getIdFactura()));
            comandaIdField.setValue(String.valueOf(factura.getComanda().getIdComanda()));
            sumaDePlataField.setValue(String.valueOf(factura.getSumaDePlata()));
            dataEmitereField.setValue(LocalDate.parse(factura.getDataEmitere()));
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        idFacturaField.clear();
        comandaIdField.clear();
        sumaDePlataField.clear();
        dataEmitereField.clear();
        grid.asSingleSelect().clear();
    }

    private List<Factura> facturiExistent = new ArrayList<>();

    private void updateGrid() {
        List<Factura> allFacturi = new ArrayList<>();
        allFacturi.addAll(facturiExistent);
        allFacturi.addAll(facturi);

        grid.setItems(allFacturi);
    }
}