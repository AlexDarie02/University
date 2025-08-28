package mcv.integration;

import diabetici.dao.AssunzioneDAO;
import diabetici.dao.DatabaseManager;
import mcv.model.Assunzione;
import mcv.model.AssunzioneService;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssunzioniIntegrationTest {

    private static Connection connection;
    private AssunzioneDAO assunzioneDAO;
    private AssunzioneService assunzioneService;

    @BeforeAll
    public static void connettiAlDatabase() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    @AfterAll
    public static void chiudiConnessione() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    public void setup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                INSERT INTO pazienti (
                    codice_fiscale, email, nome, cognome, ruolo, genere, password,
                    ulter_info, data_nascita, codice_fiscale_medico,
                    fattori_rischio, patologie_pregresse, comorbidita
                )
                VALUES (
                    'RSSMRA80A01H501U', 'anna.martini@example.com', 'Anna', 'Martini', 'paziente', 'F', 'test123',
                    NULL, '1980-01-01', NULL,
                    'Fumatore', NULL, 'Ipertensione'
                )
            """);
        }

        assunzioneDAO = new AssunzioneDAO(connection);
        assunzioneService = new AssunzioneService(assunzioneDAO);
    }

    @AfterEach
    public void cleanup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM assunzioni WHERE codice_fiscale = 'RSSMRA80A01H501U'");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'RSSMRA80A01H501U'");
        }
    }

    @Test
    public void testSalvataggioAssunzione() {
        // Provo a salvare un'assunzione per il paziente
        Assunzione a = new Assunzione("RSSMRA80A01H501U", "Metformina", "500", LocalDateTime.now());

        assunzioneService.salvaAssunzione(a);

        List<Assunzione> lista = assunzioneService.getAssunzioniPerPaziente("RSSMRA80A01H501U");

        assertFalse(lista.isEmpty());
        assertEquals("Metformina", lista.get(0).getFarmaco());
        assertEquals("500", lista.get(0).getDosaggio());
    }

    @Test
    public void testGetAssunzioniInsulina() {
        // Provo a salvare un'assunzione di insulina e poi recuperarla
        Assunzione insulina = new Assunzione("RSSMRA80A01H501U", "Insulina", "10", LocalDateTime.now().minusDays(2));
        assunzioneService.salvaAssunzione(insulina);

        List<Assunzione> insuline = assunzioneService.getAssunzioniInsulina("RSSMRA80A01H501U", "ULTIMA_SETTIMANA");

        assertFalse(insuline.isEmpty());
        assertEquals("Insulina", insuline.get(0).getFarmaco());
    }
}
