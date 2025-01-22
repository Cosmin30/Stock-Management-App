package org.classes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;


import java.util.Arrays;

public class TestPersistenta {
    public static void main(String[] args) {
        // Configurarea EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("myJpaUnit");
        EntityManager em = emf.createEntityManager();

        try {
            // Începe o tranzacție
            em.getTransaction().begin();

            // --- Test 1: Persistează 5 obiecte de tip Produs ---
            Produs produs1 = new Produs("Laptop", "Laptop performant", 3000.0, 10);
            Produs produs2 = new Produs("Telefon", "Smartphone Android", 1500.0, 20);
            Produs produs3 = new Produs("Televizor", "Smart TV 4K", 2500.0, 15);
            Produs produs4 = new Produs("Tableta", "Tableta Samsung", 1200.0, 30);
            Produs produs5 = new Produs("Casti", "Casti wireless", 400.0, 50);

            // Persistarea produselor
            persistProdus(em, produs1);
            persistProdus(em, produs2);
            persistProdus(em, produs3);
            persistProdus(em, produs4);
            persistProdus(em, produs5);

            // --- Test 2: Persistează 5 obiecte de tip Client ---
            Client client1 = new Client("Ion Popescu", "Strada Victoriei, 12", "0723123456", "ion@email.com");
            Client client2 = new Client("Maria Ionescu", "Strada Unirii, 15", "0734123456", "maria@email.com");
            Client client3 = new Client("Andrei Vasile", "Strada Libertatii, 10", "0745123456", "andrei@email.com");
            Client client4 = new Client("Elena Georgescu", "Strada Aviatorilor, 8", "0756123456", "elena@email.com");
            Client client5 = new Client("Gabriela Stan", "Strada Eroilor, 14", "0767123456", "gabriela@email.com");

            // Persistarea clientilor
            persistClient(em, client1);
            persistClient(em, client2);
            persistClient(em, client3);
            persistClient(em, client4);
            persistClient(em, client5);

            // --- Test 3: Persistează 5 obiecte de tip Comanda ---
            Comanda comanda1 = new Comanda(1, client1, Arrays.asList(produs1, produs2), "03.12.2024", 4500.0);
            Comanda comanda2 = new Comanda(2, client2, Arrays.asList(produs2, produs3), "04.12.2024", 4000.0);
            Comanda comanda3 = new Comanda(3, client3, Arrays.asList(produs3, produs4), "05.12.2024", 3700.0);
            Comanda comanda4 = new Comanda(4, client4, Arrays.asList(produs4, produs5), "06.12.2024", 3200.0);
            Comanda comanda5 = new Comanda(5, client5, Arrays.asList(produs5, produs1), "07.12.2024", 3400.0);

            // Persistarea comenzilor
            persistComanda(em, comanda1);
            persistComanda(em, comanda2);
            persistComanda(em, comanda3);
            persistComanda(em, comanda4);
            persistComanda(em, comanda5);

            // --- Test 4: Persistează 5 obiecte de tip Factura ---
            Factura factura1 = new Factura(1, comanda1, 4500.0, "03-12-2024");
            Factura factura2 = new Factura(2, comanda2, 4000.0, "04-12-2024");
            Factura factura3 = new Factura(3, comanda3, 3700.0, "05-12-2024");
            Factura factura4 = new Factura(4, comanda4, 3200.0, "06-12-2024");
            Factura factura5 = new Factura(5, comanda5, 3400.0, "07-12-2024");

            // Persistarea facturilor
            persistFactura(em, factura1);
            persistFactura(em, factura2);
            persistFactura(em, factura3);
            persistFactura(em, factura4);
            persistFactura(em, factura5);

            // --- Test 5: Persistează 5 obiecte de tip Plata ---
            Plata plata1 = new Plata(1, factura1, 4500.00, "03-12-2024", "Card");
            Plata plata2 = new Plata(2, factura2, 4000.00, "04-12-2024", "Cash");
            Plata plata3 = new Plata(3, factura3, 3700.00, "05-12-2024", "Card");
            Plata plata4 = new Plata(4, factura4, 3200.00, "06-12-2024", "Cash");
            Plata plata5 = new Plata(5, factura5, 3400.00, "07-12-2024", "Card");

            // Persistarea platilor
            persistPlata(em, plata1);
            persistPlata(em, plata2);
            persistPlata(em, plata3);
            persistPlata(em, plata4);
            persistPlata(em, plata5);

            // Confirmă tranzacția
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    // Metoda pentru a persista un produs
    private static void persistProdus(EntityManager em, Produs produs) {
        if (!existaProdus(em, produs.getNume())) {
            em.persist(produs);
            System.out.println("Produsul " + produs.getNume() + " a fost persistat.");
        } else {
            System.out.println("Produsul " + produs.getNume() + " există deja în baza de date.");
        }
    }

    // Metoda pentru a persista un client
    private static void persistClient(EntityManager em, Client client) {
        if (!existaClient(em, client.getEmail())) {
            em.persist(client);
            System.out.println("Clientul " + client.getNume() + " a fost persistat.");
        } else {
            System.out.println("Clientul " + client.getNume() + " există deja în baza de date.");
        }
    }

    // Metoda pentru a persista o comandă
    private static void persistComanda(EntityManager em, Comanda comanda) {
        if (!existaComanda(em, comanda.getIdComanda())) {
            em.persist(comanda);
            System.out.println("Comanda " + comanda.getIdComanda() + " a fost persistata.");
        } else {
            System.out.println("Comanda " + comanda.getIdComanda() + " există deja în baza de date.");
        }
    }

    // Metoda pentru a persista o factură
    private static void persistFactura(EntityManager em, Factura factura) {
        if (!existaFactura(em, factura.getIdFactura())) {
            em.persist(factura);
            System.out.println("Factura " + factura.getIdFactura() + " a fost persistata.");
        } else {
            System.out.println("Factura " + factura.getIdFactura() + " există deja în baza de date.");
        }
    }

    // Metoda pentru a persista o plată
    private static void persistPlata(EntityManager em, Plata plata) {
        if (!existaPlata(em, plata.getIdPlata())) {
            em.persist(plata);
            System.out.println("Plata " + plata.getIdPlata() + " a fost persistata.");
        } else {
            System.out.println("Plata " + plata.getIdPlata() + " există deja în baza de date.");
        }
    }

    // Metoda pentru a verifica existența unui produs în baza de date
    private static boolean existaProdus(EntityManager em, String numeProdus) {
        TypedQuery<Produs> query = em.createQuery("SELECT p FROM Produs p WHERE p.nume = :nume", Produs.class);
        query.setParameter("nume", numeProdus);
        return !query.getResultList().isEmpty();
    }

    // Metoda pentru a verifica existența unui client în baza de date
    private static boolean existaClient(EntityManager em, String email) {
        TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c WHERE c.email = :email", Client.class);
        query.setParameter("email", email);
        return !query.getResultList().isEmpty();
    }

    // Metoda pentru a verifica existența unei comenzi în baza de date
    private static boolean existaComanda(EntityManager em, int idComanda) {
        TypedQuery<Comanda> query = em.createQuery("SELECT c FROM Comanda c WHERE c.idComanda = :idComanda", Comanda.class);
        query.setParameter("idComanda", idComanda);
        return !query.getResultList().isEmpty();
    }

    // Metoda pentru a verifica existența unei facturi în baza de date
    private static boolean existaFactura(EntityManager em, int idFactura) {
        TypedQuery<Factura> query = em.createQuery("SELECT f FROM Factura f WHERE f.idFactura = :idFactura", Factura.class);
        query.setParameter("idFactura", idFactura);
        return !query.getResultList().isEmpty();
    }

    // Metoda pentru a verifica existența unei plăți în baza de date
    private static boolean existaPlata(EntityManager em, int idPlata) {
        TypedQuery<Plata> query = em.createQuery("SELECT p FROM Plata p WHERE p.idPlata = :idPlata", Plata.class);
        query.setParameter("idPlata", idPlata);
        return !query.getResultList().isEmpty();
    }
}
