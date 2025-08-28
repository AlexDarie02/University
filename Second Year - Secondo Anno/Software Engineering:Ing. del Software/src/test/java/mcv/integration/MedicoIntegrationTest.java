package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.MedicoDAO;
import mcv.model.Medico;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class MedicoIntegrationTest {

    private static Connection connection;
    private static MedicoDAO medicoDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DatabaseManager.getConnection();
        medicoDAO = new MedicoDAO(connection);

    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale IN ('TESTCF01', 'TESTCF02')");
        }
    }

    @AfterAll
    static void teardown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale IN ('TESTCF01', 'TESTCF02')");
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testGetMedicoByCodiceFiscale() throws SQLException {
        // Provo a inserire un medico e poi recuperarlo
        Medico medico = new Medico("TESTCF01", "TestNome", "TestCognome", "test@example.com", "medico", "pwd");
        medicoDAO.inserisciMedico(medico);

        Medico trovato = medicoDAO.getMedicoByCodiceFiscale("TESTCF01");
        assertNotNull(trovato);
        assertEquals("TestNome", trovato.getNome());
    }

    @Test
    void testSuggerisciCodiciFiscali() throws SQLException {
        // Provo a vedere se mi suggerisce i codici fiscali
        medicoDAO.inserisciMedico(new Medico("TESTCF01", "TestNome", "TestCognome", "test@example.com", "medico", "pwd"));
        medicoDAO.inserisciMedico(new Medico("TESTCF02", "MedicoTest", "CongTest", "altro@example.com", "medico", "pwd"));

        var risultati = medicoDAO.suggerisciCodiciFiscali("TEST");
        assertTrue(risultati.contains("TESTCF01"));
        assertTrue(risultati.contains("TESTCF02"));
    }

}
