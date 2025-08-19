package mcv.model;

import java.time.LocalDate;

public class Prescrizione {
    private final int id;
    private final String nomeFarmaco, dosaggio, frequenza, indicazioni;
    private final LocalDate dataPrescrizione;

    public Prescrizione(int id, String nomeFarmaco, String dosaggio, String frequenza,
                        String indicazioni, LocalDate dataPrescrizione) {
        this.id = id;
        this.nomeFarmaco = nomeFarmaco;
        this.dosaggio = dosaggio;
        this.frequenza = frequenza;
        this.indicazioni = indicazioni;
        this.dataPrescrizione = dataPrescrizione;
    }
    public int getId() {
        return id;
    }

    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    public String getDosaggio() {
        return dosaggio;
    }

    public String getFrequenza() {
        return frequenza;
    }

    public String getIndicazioni() {
        return indicazioni;
    }

    public LocalDate getDataPrescrizione() {
        return dataPrescrizione;
    }
}
