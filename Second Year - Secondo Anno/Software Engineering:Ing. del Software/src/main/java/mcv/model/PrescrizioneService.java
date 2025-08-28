package mcv.model;

import diabetici.dao.LogDAO;
import diabetici.dao.PrescrizioneDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrescrizioneService {

    private final PrescrizioneDAO dao;
    private final LogDAO logDAO;

    public PrescrizioneService(PrescrizioneDAO dao, LogDAO logDAO) {
        this.dao = dao;
        this.logDAO = logDAO;
    }

    public List<Prescrizione> getPrescrizioniPerPaziente(String cf) {
        try {
            return dao.getPrescrizioniByPaziente(cf);
        } catch (SQLException e) {
            throw new RuntimeException("Errore recupero prescrizioni", e);
        }
    }

    public Prescrizione aggiungiPrescrizione(String cfPaziente, String farmaco, String dosaggio, String frequenza, String indicazioni, Medico medico) {
        try {
            Prescrizione nuova = new Prescrizione(0, farmaco, dosaggio, frequenza, indicazioni, LocalDate.now());
            Prescrizione p = dao.aggiungiPrescrizione(cfPaziente, nuova);

            String azione = "Inserita prescrizione: " + farmaco + " (" + dosaggio + ", " + frequenza + ")";
            logDAO.inserisciLog(new LogAzione(medico.getCodiceFiscale(), cfPaziente, azione, java.time.LocalDateTime.now()));

            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiunta prescrizione", e);
        }
    }

    public boolean aggiornaPrescrizione(Prescrizione p, String cfPaziente, Medico medico) {
        try {
            boolean result = dao.aggiornaPrescrizione(p);
            if (result) {
                String azione = "Aggiornata prescrizione ID: " + p.getId() + " - Farmaco: " + p.getNomeFarmaco();
                logDAO.inserisciLog(new LogAzione(medico.getCodiceFiscale(), cfPaziente, azione, java.time.LocalDateTime.now()));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Errore aggiornamento prescrizione", e);
        }
    }

    public boolean eliminaPrescrizione(int id, String cfPaziente, Medico medico) {
        try {
            boolean result = dao.eliminaPrescrizione(id);
            if (result) {
                String azione = "Eliminata prescrizione ID: " + id;
                logDAO.inserisciLog(new LogAzione(medico.getCodiceFiscale(), cfPaziente, azione, java.time.LocalDateTime.now()));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Errore eliminazione prescrizione", e);
        }
    }

}
