package org.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Plata {
    @Id
    private int idPlata;

    @ManyToOne
    private Factura factura;

    private double sumaPlatita;
    private String dataPlatii;
    private String metodaPlata;
}
