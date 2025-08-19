package mcv.model;

import java.time.LocalDateTime;

public class Sintomo {
    private String cfPaziente;
    private String sintomo;
    private LocalDateTime dataOra;

    public Sintomo(String cfPaziente, String sintomo, LocalDateTime dataOra) {
        this.cfPaziente = cfPaziente;
        this.sintomo = sintomo;
        this.dataOra = dataOra;
    }

    public String getCfPaziente() {
        return cfPaziente;
    }

    public void setCfPaziente(String cfPaziente) {
        this.cfPaziente = cfPaziente;
    }

    public String getSintomo() {
        return sintomo;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    @Override
    public String toString() {
        return sintomo + " (" + dataOra + ")";
    }
}
