package mcv.model;

import diabetici.dao.NotificaDAO;
import mcv.factory.ServiceFactory;
import mcv.session.UserSession;

import java.sql.SQLException;
import java.util.List;

public class NotificaHandler {
    private final NotificaDAO dao;

    public NotificaHandler(NotificaDAO dao) {
        this.dao = dao;
    }

    public void addNotifica(Notifica n) throws SQLException {
        dao.save(n);
    }

    public List<Notifica> getAllNotifiche() throws SQLException {
        return dao.findAll();
    }

    public void rispondiNotifica(int id, String stato) throws SQLException {
        dao.aggiornaStato(id, stato);
    }


    public void eliminaNotifichePerMedico() {
        String medicoCF = UserSession.getInstance().getLoggedInUserCF();
        dao.eliminaNotificheMedico(medicoCF);
    }

    public void eliminaNotifichePerPaziente(String codiceFiscalePaziente) throws SQLException {
        dao.eliminaNotifichePerPaziente(codiceFiscalePaziente);
    }

    public static void inviaNotifica(Notifica notifica) {
        try {
            NotificaHandler handler = ServiceFactory.getInstance().getNotificaHandler();
            handler.addNotifica(notifica);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notifica> getNotificheByMedico(String medicoCF) throws SQLException {
        return dao.findNotificheByMedico(medicoCF);
    }







}


