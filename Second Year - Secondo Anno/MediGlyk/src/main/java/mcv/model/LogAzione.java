package mcv.model;

import java.time.LocalDateTime;

public class LogAzione {
    private final String medicoCF;
    private final String pazienteCF;
    private final String azione;
    private final LocalDateTime dataOra;

    public LogAzione(String medicoCF, String pazienteCF, String azione, LocalDateTime dataOra) {
        this.medicoCF = medicoCF;
        this.pazienteCF = pazienteCF;
        this.azione = azione;
        this.dataOra = dataOra;
    }

    public String getMedicoCF() {
        return medicoCF;
    }

    public String getPazienteCF() {
        return pazienteCF;
    }

    public String getAzione() {
        return azione;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }
}
