package mcv.model;

import diabetici.dao.RilevazioneDAO;
import mcv.factory.ServiceFactory;

import java.sql.SQLException;
import java.util.List;

public class StoricoRilevazioniManager {

    private final RilevazioneDAO rilevazioneDAO;

    public StoricoRilevazioniManager() throws SQLException {
        this.rilevazioneDAO = new RilevazioneDAO(ServiceFactory.getInstance().getConnection());
    }

    public List<Rilevazione> getRilevazioniByPaziente(String codiceFiscale) throws SQLException {
        return rilevazioneDAO.getRilevazioniByPaziente(codiceFiscale);
    }
    }


