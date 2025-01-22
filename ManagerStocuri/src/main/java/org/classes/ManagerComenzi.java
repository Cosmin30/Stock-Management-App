package org.classes;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerComenzi {
    private List<Comanda> comenzi;

    public void adaugaComanda(Comanda comanda) {
        comenzi.add(comanda);
    }
    public void stergeComanda(Comanda comanda) {
        comenzi.remove(comanda);
    }
    public Comanda cautaComanda(int idComanda) {
        return comenzi.stream()
                .filter(c -> c.getIdComanda() == idComanda)
                .findFirst()
                .orElse(null);
    }



}
