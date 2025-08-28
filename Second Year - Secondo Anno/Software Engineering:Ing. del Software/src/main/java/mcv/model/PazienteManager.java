package mcv.model;

import diabetici.dao.LogDAO;
import diabetici.dao.PazienteDAO;

import java.sql.SQLException;
import java.util.List;

public class PazienteManager {
    private final PazienteDAO dao;
    private final LogDAO logDAO;

    public PazienteManager(PazienteDAO dao, LogDAO logDAO) {
        this.dao = dao;
        this.logDAO = logDAO;
    }

    public List<Paziente> getAllPazienti() throws SQLException {
        return dao.findAll();
    }

    public boolean aggiornaMedicoCurante(String cfPaziente, String cfMedico) throws SQLException {
        return dao.aggiornaMedicoCurante(cfPaziente, cfMedico);
    }

    public Paziente getByCodiceFiscale(String cf) {
        try {
            return dao.findByCodiceFiscale(cf);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean aggiornaInformazioniSanitarie(String cfPaziente, String fattori, String patologie, String comorbidita, Medico medico) throws SQLException {
        boolean ok = dao.aggiornaInfoSanitarie(cfPaziente, fattori, patologie, comorbidita);
        if (ok) {
            String azione = "Aggiornate informazioni sanitarie: fattori=[" + fattori + "], patologie=[" + patologie + "], comorbidità=[" + comorbidita + "]";
            logDAO.inserisciLog(new LogAzione(medico.getCodiceFiscale(), cfPaziente, azione, java.time.LocalDateTime.now()));
        }
        return ok;
    }

    public boolean aggiornaInfoAggiuntive(String cf, String info) {
        try {
            return dao.aggiungiInfoAggiuntive(cf, info);
        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento info aggiuntive", e);
        }
    }

    public boolean setUlterInfo(String codiceFiscale, String nuovoUlterInfo) {
        try {
            return dao.setUlterInfo(codiceFiscale, nuovoUlterInfo);
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'aggiornamento delle info aggiuntive", e);
        }
    }

    public List<String> suggerisciPazienti(String filtro) {
        return dao.suggerisciCodiciFiscali(filtro);
    }

}
