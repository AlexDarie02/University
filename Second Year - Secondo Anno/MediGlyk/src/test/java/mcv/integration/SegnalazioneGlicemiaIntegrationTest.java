package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.SegnalazioneGlicemiaDAO;
import mcv.model.SegnalazioneGlicemia;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SegnalazioneGlicemiaIntegrationTest {

    private static Connection connection;
    private static SegnalazioneGlicemiaDAO segnalazioneDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DatabaseManager.getConnection();
        segnalazioneDAO = new SegnalazioneGlicemiaDAO(connection);
    }

    @BeforeEach
    void cleanBeforeEach() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM segnalazioni WHERE cf_paziente IN ('TESTCF01', 'TESTCF02')");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale IN ('TESTCF01', 'TESTCF02')");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'MEDICO01'");

            stmt.executeUpdate("""
                INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
                VALUES ('MEDICO01', 'Franco', 'Rossi', 'franco@example.com', 'medico', '1234')
            """);

            stmt.executeUpdate("""
                INSERT INTO pazienti (codice_fiscale, nome, cognome, email, ruolo, password, genere, data_nascita, ulter_info, codice_fiscale_medico, fattori_rischio, patologie_pregresse, comorbidita)
                VALUES
                ('TESTCF01', 'Anna', 'Bianchi', 'anna@example.com', 'paziente', 'pass1', 'F', '1990-01-01', '', 'MEDICO01', '', '', ''),
                ('TESTCF02', 'Luca', 'Verdi', 'luca@example.com', 'paziente', 'pass2', 'M', '1985-05-05', '', 'MEDICO01', '', '', '')
            """);
        }
    }

    @AfterAll
    static void teardown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM segnalazioni WHERE cf_paziente IN ('TESTCF01', 'TESTCF02')");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale IN ('TESTCF01', 'TESTCF02')");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'MEDICO01'");
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testInserisciSegnalazioni() throws SQLException {
        // Provo a inserire segnalazioni di glicemia
        SegnalazioneGlicemia seg1 = new SegnalazioneGlicemia(
                "TESTCF01", "PREPASTO", 150.0, LocalDateTime.now().minusDays(1), "GLICEMIA_ALTA"
        );
        SegnalazioneGlicemia seg2 = new SegnalazioneGlicemia(
                "TESTCF02", "POSTPASTO", 170.0, LocalDateTime.now(), "TERAPIA_EXTRA"
        );

        boolean inserito1 = segnalazioneDAO.inserisciSegnalazione(seg1, "MEDICO01");
        boolean inserito2 = segnalazioneDAO.inserisciSegnalazione(seg2, "MEDICO01");

        assertTrue(inserito1);
        assertTrue(inserito2);
    }

    @Test
    void testGetSegnalazioniPerMedico() throws SQLException {
        // Provo a recuperare le segnalazioni per un medico, per CF paziente
        SegnalazioneGlicemia seg1 = new SegnalazioneGlicemia(
                "TESTCF01", "PREPASTO", 150.0, LocalDateTime.now().minusDays(1), "GLICEMIA_ALTA"
        );
        SegnalazioneGlicemia seg2 = new SegnalazioneGlicemia(
                "TESTCF02", "POSTPASTO", 170.0, LocalDateTime.now(), "TERAPIA_EXTRA"
        );

        segnalazioneDAO.inserisciSegnalazione(seg1, "MEDICO01");
        segnalazioneDAO.inserisciSegnalazione(seg2, "MEDICO01");

        List<SegnalazioneGlicemia> lista = segnalazioneDAO.getSegnalazioniPerMedico("MEDICO01");

        assertNotNull(lista);
        assertTrue(lista.stream().anyMatch(s -> s.getCfPaziente().equals("TESTCF01") && s.getTipoMisurazione().equals("PREPASTO")));
        assertTrue(lista.stream().anyMatch(s -> s.getCfPaziente().equals("TESTCF02") && s.getTipoMisurazione().equals("POSTPASTO")));
    }
}
