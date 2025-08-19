package mcv.model;

public class Utente {
    private final String email;
    private final String codiceFiscale;
    private final String ruolo;
    private final String password;

    public Utente(String codiceFiscale, String email, String ruolo, String password) {
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.ruolo = ruolo;
        this.password = password;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getEmail() {
        return email;
    }

    public String getRuolo() {
        return ruolo;
    }

    public String getPassword() {
        return password;
    }

}
