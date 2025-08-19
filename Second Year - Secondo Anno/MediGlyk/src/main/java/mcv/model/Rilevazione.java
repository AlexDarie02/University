package mcv.model;

import java.time.LocalDateTime;

public class Rilevazione {

    private final String codiceFiscale;
    private final int valore;
    private final LocalDateTime dataOra;
    private final String tipo;
    private final String tipoPasto;

    public Rilevazione(String codiceFiscale, int valore, LocalDateTime dataOra, String tipo, String tipoPasto) {
        this.codiceFiscale = codiceFiscale;
        this.valore = valore;
        this.dataOra = dataOra;
        this.tipo = tipo;
        this.tipoPasto = tipoPasto;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public int getValore() {
        return valore;
    }

    public String getTipoPasto() {
        return tipoPasto;
    }
}
