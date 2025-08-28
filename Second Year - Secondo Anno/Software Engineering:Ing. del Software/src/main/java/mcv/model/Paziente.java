package mcv.model;

import java.time.LocalDate;

public class Paziente extends Utente {

    private final String nome, cognome, genere;
    private String ulter_info;
    private final LocalDate dataNascita;
    private final Medico medicoRif;
    private final String fattoriRischio;
    private final String patologiePregresse;
    private final String comorbidita;

    public Paziente(String nome, String cognome, String codiceFiscale, String email, String ruolo,
                    String genere, String password, String ulter_info, LocalDate dataNascita, Medico medico_riferimento,
                    String fattoriRischio, String patologiePregresse, String comorbidita) {
        super(codiceFiscale, email, ruolo, password);
        this.nome = nome;
        this.cognome = cognome;
        this.genere = genere;
        this.ulter_info = ulter_info;
        this.dataNascita = dataNascita;
        this.medicoRif = medico_riferimento;
        this.fattoriRischio = fattoriRischio;
        this.patologiePregresse = patologiePregresse;
        this.comorbidita = comorbidita;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getGenere() {
        return genere;
    }

    public String getUlter_info() {
        return ulter_info;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public Medico getMedicoRiferimento() {
        return medicoRif;
    }

    public String getFattoriRischio() {
        return fattoriRischio;
    }

    public String getPatologiePregresse() {
        return patologiePregresse;
    }

    public String getComorbidita() {
        return comorbidita;
    }

    public void setUlter_info(String ulter_info) {
        this.ulter_info = ulter_info;
    }

}
