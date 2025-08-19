package mcv.model;

import java.time.LocalDateTime;

public class SegnalazioneGlicemia {

    private final String cfPaziente;
    private final String tipoMisurazione; // PREPASTO o POSTPASTO
    private final double valore;
    private final LocalDateTime dataOra;
    private final String categoria; // GLICEMIA_ALTA, TERAPIA_EXTRA

    public SegnalazioneGlicemia(String cfPaziente, String tipoMisurazione, double valore, LocalDateTime dataOra, String categoria) {
        this.cfPaziente = cfPaziente;
        this.tipoMisurazione = tipoMisurazione;
        this.valore = valore;
        this.dataOra = dataOra;
        this.categoria = categoria;
    }

    public String getCfPaziente() {
        return cfPaziente;
    }

    public String getTipoMisurazione() {
        return tipoMisurazione;
    }

    public double getValore() {
        return valore;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public String getCategoria() {
        return categoria;
    }
}
