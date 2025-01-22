package org.classes;
import java.util.*;

public class RaportStocuri {
    private List<Produs> produse;

    public RaportStocuri(List<Produs> produse) {
        this.produse = produse;
    }

    public void genereazaRaport() {
        System.out.println("Raport Stocuri:");
        for (Produs produs : produse) {
            System.out.println(produs.getNume() + " - Cantitate in stoc: " + produs.getCantitateInStoc());
        }
    }
}
