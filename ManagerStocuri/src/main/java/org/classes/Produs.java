package org.classes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class Produs {

    @Id
    private String nume;  // Folosirea numelui ca identificator

    private String descriere;
    private double pret;
    private int cantitateInStoc;

}
