package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.SintomoDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SintomoIntegrationTest {

    private static Connection connection;
    private SintomoDAO sintomoDAO;

    @BeforeAll
    static void setupDatabase() throws Exception {
        connection = DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM sintomi_segnalati WHERE cf_paziente = 'PAZTEST01'");
        }
    }

    @AfterAll
    static void cleanDatabase() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM sintomi_segnalati WHERE cf_paziente = 'PAZTEST01'");
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void init() {
        sintomoDAO = new SintomoDAO(connection);
    }

    @Test
    void testInserisciSintomo() throws SQLException {
        // Provo a inserire un sintomo per un paziente
        sintomoDAO.inserisciSintomo("PAZTEST01", "Febbre");

        List<String> sintomi = sintomoDAO.getSintomiSegnalatiUltime24h("PAZTEST01");

        assertNotNull(sintomi);
        assertTrue(sintomi.contains("Febbre"));
    }

    @Test
    void testGetSintomiSoloUltime24h() throws SQLException {
        // Provo a inserire un sintomo recente e uno vecchio, e verifico che solo il recente sia presente
        sintomoDAO.inserisciSintomo("PAZTEST01", "Tosse");

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
            INSERT INTO sintomi_segnalati (cf_paziente, sintomo, data_ora)
            VALUES ('PAZTEST01', 'SintomoVecchio', NOW() - INTERVAL 2 DAY)
        """);
        }

        List<String> sintomi = sintomoDAO.getSintomiSegnalatiUltime24h("PAZTEST01");

        // Deve contenere solo quello recente
        assertTrue(sintomi.contains("Tosse"));
        assertFalse(sintomi.contains("SintomoVecchio"));
    }


}
