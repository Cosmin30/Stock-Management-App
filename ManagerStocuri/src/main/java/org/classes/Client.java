package org.classes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Client")
@Data  // Lombok pentru generarea automată a getter-elor și setter-elor
@AllArgsConstructor  // Lombok pentru constructor cu toate atributele
@NoArgsConstructor   // Lombok pentru constructor fără parametri

public class Client {
    @Id
    private String nume;

    private String adresa;
    private String telefon;
    private String email;

    // Constructori, getters și setters sunt generate de Lombok
}
