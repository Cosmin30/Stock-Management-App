package org.classes;

import java.util.List;

public class ManagerStocuri {
    private List<Produs> produse;

    public ManagerStocuri(List<Produs> produse) {
        this.produse = produse;
    }

    public void adaugaProdus(Produs produs) {
        produse.add(produs);  // Adaugă produsul în lista de produse
    }

    public void actualizeazaStoc(Produs produs, int cantitate) {
        produs.setCantitateInStoc(produs.getCantitateInStoc() + cantitate);
    }

    public Produs cautaProdus(String nume) {
        return produse.stream()
                .filter(p -> p.getNume().equalsIgnoreCase(nume))
                .findFirst()
                .orElse(null);
    }
    public void stergeProdus(Produs produs) {
        produse.remove(produs); // Assuming 'produse' is the list of products
    }

    public List<Produs> getProduse() {
        return produse;
    }
}