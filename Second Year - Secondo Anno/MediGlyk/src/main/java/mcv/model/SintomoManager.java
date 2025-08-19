package mcv.model;

import diabetici.dao.SintomoDAO;

import java.sql.SQLException;
import java.util.List;

public class SintomoManager {

    private final SintomoDAO sintomoDAO;

    public SintomoManager(SintomoDAO sintomoDAO) {
        this.sintomoDAO = sintomoDAO;
    }

    public List<String> getSintomiSegnalatiUltime24h(String cfPaziente) throws SQLException {
        return sintomoDAO.getSintomiSegnalatiUltime24h(cfPaziente);
    }

    public void inserisciSintomo(Sintomo sintomo) throws SQLException {
        sintomoDAO.inserisciSintomo(sintomo.getCfPaziente(), sintomo.getSintomo());
    }
}
