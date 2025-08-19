package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.LogDAO;
import diabetici.dao.PazienteDAO;
import mcv.model.*;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PazienteIntegrationTest {

    private static Connection connection;
    private PazienteDAO pazienteDAO;
    private PazienteManager pazienteManager;

    @BeforeAll
    public static void connetti() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    @AfterAll
    public static void chiudiConnessione() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    @BeforeEach
    public void setup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'TESTCF12345'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'MEDICO1'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'VRWHB4892BDSP'");
            stmt.executeUpdate("DELETE FROM log_azioni WHERE codice_fiscale_paziente = 'TESTCF12345'");

            stmt.executeUpdate("""
            INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
            VALUES ('MEDICO1', 'Giuseppe', 'Verdi', 'medico1@example.com', 'medico', 'password')
        """);

            stmt.executeUpdate("""
            INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
            VALUES ('VRWHB4892BDSP', 'Marta', 'Giuliari', 'marta.giuliari@example.com', 'medico', '12test')
        """);

            stmt.executeUpdate("""
            INSERT INTO pazienti (
                codice_fiscale, nome, cognome, email, ruolo, password, genere,
                data_nascita, ulter_info, codice_fiscale_medico,
                fattori_rischio, patologie_pregresse, comorbidita
            ) VALUES (
                'TESTCF12345', 'Mario', 'Rossi', 'mario@example.com', 'paziente', 'pass123', 'M',
                '1985-03-10', '', 'MEDICO1',
                '', '', ''
            )
        """);
        }
        pazienteDAO = new PazienteDAO(connection);
        pazienteManager = new PazienteManager(pazienteDAO, new LogDAO(connection));
    }

    @AfterEach
    public void cleanup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM log_azioni WHERE codice_fiscale_paziente = 'TESTCF12345'");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'TESTCF12345'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale IN ('MEDICO1', 'VRWHB4892BDSP')");
        }
    }

    @Test
    public void testRecuperoPaziente() {
         // Provo a recuperare il paziente inserito
        Paziente paziente = pazienteManager.getByCodiceFiscale("TESTCF12345");

        assertNotNull(paziente);
        assertEquals("Mario", paziente.getNome());
        assertEquals("MEDICO1", paziente.getMedicoRiferimento().getCodiceFiscale());
    }

    @Test
    public void testAggiornaInfoSanitarie() throws SQLException {
        // Provo ad aggiornare le informazioni sanitarie del paziente
        Medico medico = new Medico("VRWHB4892BDSP", "Marta", "Giuliari", "marta.giuliari@example.com", "medico", "12test");

        boolean result = pazienteManager.aggiornaInformazioniSanitarie(
                "TESTCF12345",
                "Fumatore, Obesità",
                "Ipertensione",
                "Asma",
                medico
        );

        assertTrue(result);

        Paziente aggiornato = pazienteManager.getByCodiceFiscale("TESTCF12345");
        assertEquals("Fumatore, Obesità", aggiornato.getFattoriRischio());
        assertEquals("Ipertensione", aggiornato.getPatologiePregresse());
        assertEquals("Asma", aggiornato.getComorbidita());
    }

    @Test
    public void testAggiuntaInfoAggiuntive() {
        // provo ad aggiungere informazioni aggiuntive al paziente
        boolean success = pazienteManager.aggiornaInfoAggiuntive("TESTCF12345", "Usa integratori");

        assertTrue(success);

        Paziente p = pazienteManager.getByCodiceFiscale("TESTCF12345");
        assertTrue(p.getUlter_info().contains("Usa integratori"));
    }
}
