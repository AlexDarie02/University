package mcv.factory;

import diabetici.dao.*;
import mcv.model.*;

import java.sql.Connection;
import java.sql.SQLException;

public class ServiceFactory {
    private static ServiceFactory instance;
    private final Connection connection;
    private final RilevazioneHandler rilevazioneHandler;
    private final SintomoDAO sintomoDAO;
    private final LogDAO logDAO;
    private final NotificaHandler notificaHandler;
    private final AssunzioneService assunzioneService;
    private final PrescrizioneService prescrizioneService;
    private final SegreteriaService segreteriaService;
    private final PazienteManager pazienteManager;
    private final CheckRilevazione checkRilevazione;
    private final MedicoManager medicoManager;
    private final MedicoDAO medicoDAO;

    private ServiceFactory() throws SQLException {
        this.connection = DatabaseManager.getConnection();

        this.sintomoDAO = new SintomoDAO(connection);
        this.medicoDAO = new MedicoDAO(connection);
        this.medicoManager = new MedicoManager(medicoDAO);
        this.logDAO = new LogDAO(connection);
        this.notificaHandler = new NotificaHandler(new NotificaDAO(connection));
        this.assunzioneService = new AssunzioneService(connection);
        this.segreteriaService = new SegreteriaService(medicoDAO, new PazienteDAO(connection));
        this.prescrizioneService = new PrescrizioneService(new PrescrizioneDAO(connection), logDAO);
        this.rilevazioneHandler = new RilevazioneHandler(new RilevazioneDAO(connection));
        this.pazienteManager = new PazienteManager(new PazienteDAO(connection), logDAO);
        this.checkRilevazione = new CheckRilevazione(new RilevazioneDAO(connection), new SegnalazioneGlicemiaDAO(connection));
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            try {
                instance = new ServiceFactory();
            } catch (SQLException e) {
                throw new RuntimeException("Errore durante la creazione della ServiceFactory", e);
            }
        }
        return instance;
    }

    public NotificaHandler getNotificaHandler() {
        return notificaHandler;
    }

    public AssunzioneService getAssunzioneService() {
        return assunzioneService;
    }

    public PrescrizioneService getPrescrizioneService() {
        return prescrizioneService;
    }

    public PazienteManager getPazienteManager() {
        return pazienteManager;
    }

    public SegreteriaService getSegreteriaService() {
        return segreteriaService;
    }

    public CheckRilevazione getCheckRilevazione() {
        return checkRilevazione;
    }

    public Connection getConnection() {
        return connection;
    }

    public RilevazioneHandler getRilevazioneHandler() {
        return rilevazioneHandler;
    }

    public MedicoManager getMedicoManager() {
        return medicoManager;
    }

    public SintomoDAO getSintomoDAO() {
        return sintomoDAO;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
