package org.classes;

import java.util.*;

public class Test {
    public static void main(String[] args) {

        Produs produs1 = new Produs("Laptop", "Laptop performant", 3000.0, 10);
        Produs produs2 = new Produs("Telefon", "Smartphone Android", 1500.0, 20);


        Client client1 = new Client("Ion Popescu", "Strada Victoriei, 12", "0723123456", "ion@email.com");


        Comanda comanda1 = new Comanda(1, client1, Arrays.asList(produs1, produs2), "03.12.2024", 4500.0);

        Factura factura1 = new Factura(1, comanda1, 4500.0, "03-12-2024");

        Plata plata1 = new Plata(1,factura1,4500.00,"03-12-2024","Card");


        // Creare manager stocuri
        ManagerStocuri managerStocuri = new ManagerStocuri(new ArrayList<>(Arrays.asList(produs1, produs2)));

        // Creare manager comenzi
        ManagerComenzi managerComenzi = new ManagerComenzi(Arrays.asList(comanda1));

        // Creare raport stocuri
        RaportStocuri raportStocuri = new RaportStocuri(managerStocuri.getProduse());
        raportStocuri.genereazaRaport();

        // Test adaugare produs in stoc
        Produs produsNou = new Produs("Mouse", "Mouse wireless", 100.0, 30);
        managerStocuri.adaugaProdus(produsNou);

        // Creare un nou raport pentru a reflecta adaugarea
        raportStocuri = new RaportStocuri(managerStocuri.getProduse());
        raportStocuri.genereazaRaport();
    }
}
