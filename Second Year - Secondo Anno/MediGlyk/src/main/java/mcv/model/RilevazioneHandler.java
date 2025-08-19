package mcv.model;

import diabetici.dao.RilevazioneDAO;
import java.sql.SQLException;
import java.util.List;

public class RilevazioneHandler {

    public final RilevazioneDAO dao;

    public RilevazioneHandler(RilevazioneDAO dao) {
        this.dao = dao;
    }

    public List<Rilevazione> getRilevazioni(String codiceFiscale, String periodo) throws SQLException {
        return dao.getRilevazioniByPeriodo(codiceFiscale, periodo);
    }



}
