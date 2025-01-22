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
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.classes.Comanda;
import org.classes.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route("comenzi")
public class ComenziView extends VerticalLayout {

    private static final Logger LOGGER = Logger.getLogger(ComenziView.class.getName());

    private List<Comanda> comenzi;
    private Grid<Comanda> grid;
    private EntityManager em;
    private Comanda selectedComanda;

    private TextField idComandaField = new TextField("ID Comandă");
    private TextField numeField = new TextField("Nume Client");
    private TextField adresaField = new TextField("Adresă");
    private TextField telefonField = new TextField("Telefon");
    private TextField emailField = new TextField("Email");
    private TextField valoareTotalaField = new TextField("Valoare Totală");
    private DatePicker dataComenziiField = new DatePicker("Data Comenzii");
    private Button saveButton = new Button("Salvează comandă");
    private Button deleteButton = new Button("Șterge comandă");

    public ComenziView() {
        try {

            em = Persistence.createEntityManagerFactory("myJpaUnit").createEntityManager();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (em.isOpen()) {
                    em.close();
                }
            }));

            populateComenziExistent();

            grid = new Grid<>(Comanda.class);
            grid.setColumns("idComanda", "valoareTotala", "dataComenzii");

            grid.addColumn(comanda -> comanda.getClient() != null ? comanda.getClient().getNume() : "N/A")
                    .setHeader("Nume Client")
                    .setSortable(true);

            grid.addColumn(comanda -> comanda.getClient() != null ? comanda.getClient().getAdresa() : "N/A")
                    .setHeader("Adresă Client")
                    .setSortable(true);

            grid.addColumn(comanda -> comanda.getClient() != null ? comanda.getClient().getTelefon() : "N/A")
                    .setHeader("Telefon Client")
                    .setSortable(true);

            grid.addColumn(comanda -> comanda.getClient() != null ? comanda.getClient().getEmail() : "N/A")
                    .setHeader("Email Client")
                    .setSortable(true);

            grid.asSingleSelect().addValueChangeListener(event -> selectComanda(event.getValue()));

            updateGrid();

            add(grid, createFormLayout());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing ComenziView", e);
            Notification.show("Eroare la inițializarea ComenziView.");
        }
    }

    private void populateComenziExistent() {
        try {

            comenzi = em.createQuery("SELECT c FROM Comanda c", Comanda.class).getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading existing orders", e);
            Notification.show("Eroare la încărcarea comenzilor existente.");
        }
    }

    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();

        saveButton.addClickListener(e -> saveComanda());
        deleteButton.addClickListener(e -> deleteComanda());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, deleteButton);
        formLayout.add(idComandaField, numeField, adresaField, telefonField, emailField, valoareTotalaField, dataComenziiField, buttonsLayout);

        return formLayout;
    }

    private void saveComanda() {
        String idComandaStr = idComandaField.getValue();
        String numeStr = numeField.getValue();
        String adresaStr = adresaField.getValue();
        String telefonStr = telefonField.getValue();
        String emailStr = emailField.getValue();
        String valoareTotalaStr = valoareTotalaField.getValue();
        LocalDate dataComenzii = dataComenziiField.getValue();

        if (numeStr.isEmpty() || adresaStr.isEmpty() || telefonStr.isEmpty() || emailStr.isEmpty() || valoareTotalaStr.isEmpty() || dataComenzii == null || idComandaStr.isEmpty()) {
            Notification.show("Toate câmpurile trebuie să fie completate!");
            return;
        }

        try {

            int idComanda = Integer.parseInt(idComandaStr);
            double valoareTotala = Double.parseDouble(valoareTotalaStr);

            Client client = em.find(Client.class, numeStr);
            if (client == null) {

                client = new Client();
                client.setNume(numeStr);
                client.setAdresa(adresaStr);
                client.setTelefon(telefonStr);
                client.setEmail(emailStr);

                em.persist(client);
            } else {

                client.setAdresa(adresaStr);
                client.setTelefon(telefonStr);
                client.setEmail(emailStr);
                em.merge(client);
            }

            em.getTransaction().begin();

            Comanda comanda = new Comanda();
            comanda.setIdComanda(idComanda);
            comanda.setClient(client);
            comanda.setValoareTotala(valoareTotala);
            comanda.setDataComenzii(dataComenzii.toString());

            Comanda existingComanda = em.find(Comanda.class, idComanda);
            if (existingComanda != null) {
                em.merge(comanda);
                Notification.show("Comandă actualizată cu succes.");
            } else {
                em.persist(comanda);
                Notification.show("Comandă adăugată cu succes.");
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
            LOGGER.log(Level.SEVERE, "Eroare la salvarea comenzii", ex);
            Notification.show("Eroare la salvarea comenzii.");
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
    }


    private void deleteComanda() {
        if (selectedComanda != null) {
            try {

                em.getTransaction().begin();

                String sqlDeleteFactura = "DELETE FROM Factura f WHERE f.comanda.idComanda = :idComanda";
                int deletedFacturiCount = em.createQuery(sqlDeleteFactura)
                        .setParameter("idComanda", selectedComanda.getIdComanda())
                        .executeUpdate();

                String sqlDeletePlata = "DELETE FROM Plata p WHERE p.factura.comanda.idComanda = :idComanda";
                int deletedPlatiCount = em.createQuery(sqlDeletePlata)
                        .setParameter("idComanda", selectedComanda.getIdComanda())
                        .executeUpdate();

                selectedComanda = em.merge(selectedComanda);
                em.remove(selectedComanda);

                em.getTransaction().commit();

                updateGrid();
                clearForm();
                Notification.show("Comanda ștearsă cu succes.");

            } catch (Exception e) {

                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                LOGGER.log(Level.SEVERE, "Eroare la ștergerea comenzii: " + e.getMessage(), e);
                Notification.show("Eroare la ștergerea comenzii: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show("Selectați o comandă pentru a o șterge.");
        }
    }



    private void selectComanda(Comanda comanda) {
        this.selectedComanda = comanda;
        if (comanda != null) {
            Client client = comanda.getClient();
            if (client != null) {
                numeField.setValue(client.getNume());
                adresaField.setValue(client.getAdresa());
                telefonField.setValue(client.getTelefon());
                emailField.setValue(client.getEmail());
            } else {

                clearForm();
            }

            idComandaField.setValue(String.valueOf(comanda.getIdComanda()));
            valoareTotalaField.setValue(String.valueOf(comanda.getValoareTotala()));
            dataComenziiField.setValue(LocalDate.parse(comanda.getDataComenzii()));
        } else {
            clearForm();
        }
    }

    private void clearForm() {

        idComandaField.clear();
        numeField.clear();
        adresaField.clear();
        telefonField.clear();
        emailField.clear();
        valoareTotalaField.clear();
        dataComenziiField.clear();
        grid.asSingleSelect().clear();
    }

    private void updateGrid() {

        populateComenziExistent();

        grid.setItems(comenzi);
    }
}