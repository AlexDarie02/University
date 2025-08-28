package mcv.model;

public class Medico extends Utente {
    private final String nome;
    private final String cognome;

    public Medico(String codiceFiscale, String nome, String cognome, String email, String ruolo, String password) {
        super(codiceFiscale, email, ruolo, password);
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

}
