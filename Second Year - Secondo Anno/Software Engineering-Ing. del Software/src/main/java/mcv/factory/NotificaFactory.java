package mcv.factory;

import mcv.model.Notifica;

import java.time.LocalDateTime;

public class NotificaFactory {

    public static Notifica creaTerapiaExtra(String pazienteCF, String descrizione, String medicoCF) {
        String msg = "Nuova terapia extra inserita: " + descrizione;
        return new Notifica(pazienteCF, msg, LocalDateTime.now(), "TERAPIA_EXTRA", medicoCF);
    }

    public static Notifica creaNessunaAssunzioneMedico(String cf, String medicoCF) {
        String msg = "Il paziente non ha registrato assunzioni da 3 giorni";
        return new Notifica(cf, msg, LocalDateTime.now(), "ASSUNZIONE_MANCANTE", medicoCF);
    }

    public static Notifica creaPromemoriaPazienteAssunzione(String cf, String medicoCF) {
        String msg = "Sono passati 2 giorni dall'ultima assunzione. Ricordati di registrarla.";
        return new Notifica(cf, msg, LocalDateTime.now(),"PROMEMORIA_ASSUNZIONE", medicoCF);
    }

    public static Notifica creaGlicemiaGrave(String pazienteCF, double valore, String tipo, String pasto, String medicoCF) {
        String msg = "Glicemia molto alta: " + valore + " mg/dl - " + tipo + " " + pasto;
        return new Notifica(pazienteCF, msg, LocalDateTime.now(), "GLICEMIA_MOLTO_ALTA", medicoCF);
    }

    public static Notifica creaGlicemiaModerata(String pazienteCF, double valore, String tipo, String pasto, String medicoCF) {
        String msg = "Glicemia sopra soglia: " + valore + " mg/dl - " + tipo + " " + pasto;
        return new Notifica(pazienteCF, msg, LocalDateTime.now(), "GLICEMIA_ALTA", medicoCF);
    }





}
