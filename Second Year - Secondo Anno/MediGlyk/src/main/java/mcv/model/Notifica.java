package mcv.model;

import java.time.LocalDateTime;

public class Notifica {
    private final int id;
    private final String pazienteCF;
    private final String messaggio;
    private final LocalDateTime timestamp;
    private final String tipo;
    private String statoRisposta;
    private final String medicoCF;

    public Notifica(int id, String pazienteCF, String messaggio,
                    LocalDateTime timestamp, String tipo, String statoRisposta, String medicoCF) {
        this.id = id;
        this.pazienteCF = pazienteCF;
        this.messaggio = messaggio;
        this.timestamp = timestamp;
        this.tipo = tipo;
        this.statoRisposta = statoRisposta;
        this.medicoCF = medicoCF;
    }

    public Notifica(String pazienteCF, String messaggio,
                    LocalDateTime timestamp, String tipo, String medicoCF) {
        this(0, pazienteCF, messaggio, timestamp, tipo, "IN_ATTESA", medicoCF);
    }

    public int getId() {
        return id;
    }
    public String getPazienteCF() {
        return pazienteCF;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public String getStatoRisposta() {
        return statoRisposta;
    }

    public String getMedicoCF() {
        return medicoCF;
    }

    }
