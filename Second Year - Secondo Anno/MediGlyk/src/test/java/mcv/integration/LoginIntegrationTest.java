package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.MedicoDAO;
import diabetici.dao.UsersDAO;
import mcv.factory.ServiceFactory;
import mcv.model.*;
import mcv.session.UserSession;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class LoginIntegrationTest {

    private static Connection connection;
    private static UsersDAO usersDAO;
    private static MedicoDAO medicoDAO;
    private static LoginHandler loginHandler;

    @BeforeAll
    static void setup() throws Exception {
        connection = DatabaseManager.getConnection();
        usersDAO = new UsersDAO();
        medicoDAO = new MedicoDAO(connection);

        ServiceFactory serviceFactory = ServiceFactory.getInstance();

        loginHandler = new LoginHandler(serviceFactory, medicoDAO, usersDAO);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
            INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
            VALUES ('MEDLOGIN01', 'Mario', 'LoginTest', 'mario.login@example.com', 'medico', 'medpass')
            ON DUPLICATE KEY UPDATE email = VALUES(email)
        """);

            stmt.executeUpdate("""
            INSERT INTO segreteria (codice_fiscale, email, ruolo, password)
            VALUES ('SEGRLOGIN01', 'segr.login@example.com', 'segreteria', 'segrpass')
            ON DUPLICATE KEY UPDATE email = VALUES(email)
        """);

            stmt.executeUpdate("""
            INSERT INTO pazienti (
                codice_fiscale, nome, cognome, email, ruolo, password, genere,
                data_nascita, ulter_info, codice_fiscale_medico,
                fattori_rischio, patologie_pregresse, comorbidita
            ) VALUES (
                'PAZLOGIN01', 'Piero', 'PazienteTest', 'piero.paziente@example.com', 'paziente', 'pazpass', 'M',
                '1990-01-01', '', 'MEDLOGIN01',
                '', '', ''
            )
            ON DUPLICATE KEY UPDATE email = VALUES(email)
        """);
        }
    }


    @AfterAll
    static void cleanup() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'MEDLOGIN01'");
            stmt.executeUpdate("DELETE FROM segreteria WHERE codice_fiscale = 'SEGRLOGIN01'");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'PAZLOGIN01'");
        }
        connection.close();
    }

    @BeforeEach
    void resetSession() {
        UserSession.getInstance().cleanUserSession();
    }

    // Provo a fare il login per ogni tipologia di utente

    @Test
    void testLoginMedicoValido() {
        Utente utente = loginHandler.verificaCredenziali("MEDLOGIN01", "medpass", "medico");
        assertNotNull(utente);
        assertEquals("MEDLOGIN01", utente.getCodiceFiscale());
        assertEquals("medico", utente.getRuolo());

        UserSession.getInstance().setLoggedInUser(utente);
        assertEquals("MEDLOGIN01", UserSession.getInstance().getLoggedInUserCF());
    }

    @Test
    void testLoginSegreteriaValido() {
        Utente utente = loginHandler.verificaCredenziali("SEGRLOGIN01", "segrpass", "segreteria");
        assertNotNull(utente);
        assertEquals("segreteria", utente.getRuolo());
    }

    @Test
    void testLoginPazienteValido() {
        Utente utente = loginHandler.verificaCredenziali("PAZLOGIN01", "pazpass", "paziente");
        assertNotNull(utente);
        assertEquals("PAZLOGIN01", utente.getCodiceFiscale());
        assertEquals("paziente", utente.getRuolo());

        UserSession.getInstance().setLoggedInUser(utente);
        assertEquals("PAZLOGIN01", UserSession.getInstance().getLoggedInUserCF());
    }

    @Test
    void testLoginMedicoFallito() {
        // Provo a fare il login con credenziali sbagliate per il medico
        Utente utente = loginHandler.verificaCredenziali("MEDLOGIN01", "wrongpass", "medico");
        assertNull(utente);
    }

    @Test
    void testLoginPazienteFallito() {
        //Provo a fare il login con credenziali sbagliate per il paziente
        Utente utente = loginHandler.verificaCredenziali("PAZLOGIN01", "wrongpass", "paziente");
        assertNull(utente);
    }

    @Test
    void testUserSessionSingletonFunziona() {
        // Provo a usare la UserSession per verificare che sia un singleton e funzioni correttamente
        UserSession session = UserSession.getInstance();
        assertNull(session.getLoggedInUser());

        Utente finto = new Medico("MEDLOGIN01", "Mario", "LoginTest", "mario.login@example.com", "medico", "medpass");
        session.setLoggedInUser(finto);

        assertEquals("MEDLOGIN01", session.getLoggedInUserCF());
        assertEquals("medico", session.getLoggedInUserRole());

        session.cleanUserSession();
        assertNull(session.getLoggedInUser());
    }
}
