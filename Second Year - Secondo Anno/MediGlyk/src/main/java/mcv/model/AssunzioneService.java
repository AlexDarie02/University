package mcv.model;

import diabetici.dao.AssunzioneDAO;
import mcv.factory.NotificaFactory;
import mcv.factory.ServiceFactory;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AssunzioneService {

    private final AssunzioneDAO dao;

    public AssunzioneService(Connection conn) {
        this.dao = new AssunzioneDAO(conn);
    }

    public AssunzioneService(AssunzioneDAO dao) {
        this.dao = dao;
    }

    public void salvaAssunzione(Assunzione assunzione) {
        dao.insertAssunzione(assunzione);
    }

    public List<Assunzione> getAssunzioniPerPaziente(String codiceFiscale) {
        return dao.getAssunzioniByPaziente(codiceFiscale);
    }

    public void controllaInattivitaAssunzioni(String codiceFiscale){
        LocalDateTime ultimaAssunzione = dao.getUltimaAssunzione(codiceFiscale);
        if (ultimaAssunzione == null) return;

        long giorni = ChronoUnit.DAYS.between(ultimaAssunzione.toLocalDate(), LocalDate.now());

        PazienteManager pazManager = ServiceFactory.getInstance().getPazienteManager();
        Paziente paziente = pazManager.getByCodiceFiscale(codiceFiscale);
        if (paziente == null) return;

        String medicoCF = paziente.getMedicoRiferimento().getCodiceFiscale();

        if (giorni >= 3) {
            Notifica notifica = NotificaFactory.creaNessunaAssunzioneMedico(codiceFiscale, medicoCF);
            NotificaHandler.inviaNotifica(notifica);
        } else if (giorni == 2) {
            Notifica notifica = NotificaFactory.creaPromemoriaPazienteAssunzione(codiceFiscale, medicoCF);
            NotificaHandler.inviaNotifica(notifica);
        }
    }

    public List<Assunzione> getAssunzioniInsulina(String cf, String periodo) {
        LocalDateTime fine = LocalDateTime.now();
        LocalDateTime inizio;

        switch (periodo) {
            case "ULTIMA_SETTIMANA":
                inizio = fine.minusWeeks(1);
                break;
            case "ULTIMO_MESE":
                inizio = fine.minusMonths(1);
                break;
            case "ULTIMI_3_MESI":
                inizio = fine.minusMonths(3);
                break;
            default:
                inizio = fine.minusWeeks(1);
        }

        return dao.getAssunzioniInsulinaByPeriodo(cf, inizio, fine);
    }




}
