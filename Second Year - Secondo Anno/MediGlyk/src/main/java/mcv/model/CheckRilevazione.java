package mcv.model;

import diabetici.dao.RilevazioneDAO;
import diabetici.dao.SegnalazioneGlicemiaDAO;
import mcv.factory.NotificaFactory;
import mcv.session.UserSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CheckRilevazione {

    private static final double MIN_PRE_PASTO = 80.0;
    private static final double MAX_PRE_PASTO = 130.0;
    private static final double MAX_POST_PASTO = 180.0;

    RilevazioneDAO rilevazioneDAO;
    SegnalazioneGlicemiaDAO segnalazioneDAO;

    public CheckRilevazione(RilevazioneDAO rilevazioneDAO, SegnalazioneGlicemiaDAO segnalazioneDAO) {
        this.rilevazioneDAO = rilevazioneDAO;
        this.segnalazioneDAO = segnalazioneDAO;
    }

    public boolean isPrePastoValido(double glicemiaPrePasto) {
        return glicemiaPrePasto >= MIN_PRE_PASTO && glicemiaPrePasto <= MAX_PRE_PASTO;
    }

    public boolean isPostPastoValido(double glicemiaPostPasto) {
        return glicemiaPostPasto <= MAX_POST_PASTO;
    }

    public void inviaSegnalazioneMedico(String cfPaziente, double valore, String tipo, LocalDateTime dataOra) {
        SegnalazioneGlicemia segnalazione = new SegnalazioneGlicemia(
                cfPaziente,
                tipo,
                valore,
                dataOra,
                "GLICEMIA_ALTA"
        );

        try {
            Paziente paziente = (Paziente) UserSession.getInstance().getLoggedInUser();
            String cfMedico = paziente.getMedicoRiferimento().getCodiceFiscale();

            segnalazioneDAO.inserisciSegnalazione(segnalazione, cfMedico);

        } catch (SQLException | ClassCastException e) {
            e.printStackTrace();
        }
    }


    public boolean insertRilevazione(Rilevazione rilevazione) {
        boolean valoriOttimali = true;

        String tipo = rilevazione.getTipo().toUpperCase();
        int valore = rilevazione.getValore();
        String pazienteCF = rilevazione.getCodiceFiscale();
        Paziente paziente = (Paziente) UserSession.getInstance().getLoggedInUser();
        String medicoCF = paziente.getMedicoRiferimento().getCodiceFiscale();

        if (tipo.equals("PREPASTO")) {
            valoriOttimali = isPrePastoValido(valore);
        } else if (tipo.equals("POSTPASTO")) {
            valoriOttimali = isPostPastoValido(valore);
        }

        if (!valoriOttimali) {
            inviaSegnalazioneMedico(rilevazione.getCodiceFiscale(), valore, tipo, rilevazione.getDataOra());
        }

        String pasto = rilevazione.getTipoPasto();

        if (tipo.equals("PREPASTO")) {
            if (valore > 160) {
                NotificaHandler.inviaNotifica(NotificaFactory.creaGlicemiaGrave(pazienteCF, valore, tipo, pasto, medicoCF));
            } else {
                NotificaHandler.inviaNotifica(NotificaFactory.creaGlicemiaModerata(pazienteCF, valore, tipo, pasto, medicoCF));
            }
        } else if (tipo.equals("POSTPASTO")) {
            if (valore > 190) {
                NotificaHandler.inviaNotifica(NotificaFactory.creaGlicemiaGrave(pazienteCF, valore, tipo, pasto, medicoCF));
            } else {
                NotificaHandler.inviaNotifica(NotificaFactory.creaGlicemiaModerata(pazienteCF, valore, tipo, pasto, medicoCF));
            }
        }

        try {
            rilevazioneDAO.insertRilevazione(rilevazione);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return valoriOttimali;
    }

    public boolean controllaOraPostPasto(LocalDateTime dataOraPre, LocalDateTime dataOraPost) {
        return dataOraPost.isAfter(dataOraPre.plusHours(2));
    }

    public Rilevazione getUltimoPrePastoSePresente(String cfPaziente, LocalDate data, String tipoPasto) {
        try {

            return rilevazioneDAO.getUltimoPrePastoPerTipo(cfPaziente, data, tipoPasto);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
