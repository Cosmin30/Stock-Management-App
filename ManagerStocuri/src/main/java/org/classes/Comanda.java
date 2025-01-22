package org.classes;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Comanda")
@Data  // Lombok pentru generarea automată a getter-elor și setter-elor
@AllArgsConstructor  // Lombok pentru constructor cu toate atributele
@NoArgsConstructor   // Lombok pentru constructor fără parametri
public class Comanda {
    @Id
    private int idComanda;

    @ManyToOne
    private Client client;

    @OneToMany
    private List<Produs> produse;

    private String dataComenzii;
    private double valoareTotala;

    // Constructori, getters și setters sunt generate de Lombok

}
