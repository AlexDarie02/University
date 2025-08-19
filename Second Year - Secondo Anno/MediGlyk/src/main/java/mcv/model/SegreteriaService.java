package mcv.model;

import diabetici.dao.MedicoDAO;
import diabetici.dao.PazienteDAO;

import java.sql.SQLException;
import java.util.List;

public class SegreteriaService {
    private final MedicoDAO medicoDAO;
    private final PazienteDAO pazienteDAO;

    public SegreteriaService(MedicoDAO medicoDAO, PazienteDAO pazienteDAO) {
        this.medicoDAO = medicoDAO;
        this.pazienteDAO = pazienteDAO;
    }

    public void aggiungiMedico(Medico m) throws SQLException {
        medicoDAO.inserisciMedico(m);
    }

    public void aggiungiPaziente(Paziente p) throws SQLException {
        pazienteDAO.inserisciPaziente(p);
    }

    public Medico getMedicoByCodiceFiscale(String codiceFiscale) throws SQLException {
        return medicoDAO.getMedicoByCodiceFiscale(codiceFiscale);
    }

    public void eliminaMedico(String codiceFiscale) throws SQLException {
        medicoDAO.eliminaMedico(codiceFiscale);
    }

    public void eliminaPaziente(String codiceFiscale) throws SQLException {
        pazienteDAO.eliminaPaziente(codiceFiscale);
    }
}
