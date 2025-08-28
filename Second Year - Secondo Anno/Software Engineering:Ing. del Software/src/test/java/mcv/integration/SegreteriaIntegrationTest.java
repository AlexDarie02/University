package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.MedicoDAO;
import diabetici.dao.PazienteDAO;
import mcv.model.Medico;
import mcv.model.Paziente;
import mcv.model.SegreteriaService;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SegreteriaIntegrationTest {

    private static Connection connection;
    private static MedicoDAO medicoDAO;
    private static PazienteDAO pazienteDAO;
    private static SegreteriaService segreteriaService;

    @BeforeAll
    static void setup() throws Exception {
        connection = DatabaseManager.getConnection();
        medicoDAO = new MedicoDAO(connection);
        pazienteDAO = new PazienteDAO(connection);
        segreteriaService = new SegreteriaService(medicoDAO, pazienteDAO);
    }

    @AfterAll
    static void cleanup() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'PAZTEST01' OR email = 'luca@example.com'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale IN ('MEDTEST01', 'MEDTEST02', 'MEDTEST03') OR email IN ('mario@example.com', 'giulia@example.com', 'anna@example.com')");
        }
        connection.close();
    }

    @BeforeEach
    void prepareTestData() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'PAZTEST01' OR email = 'luca@example.com'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale IN ('MEDTEST01', 'MEDTEST02', 'MEDTEST03') OR email IN ('mario@example.com', 'giulia@example.com', 'anna@example.com')");
        }

        medicoDAO.inserisciMedico(new Medico("MEDTEST01", "Mario", "Rossi", "mario@example.com", "medico", "pass123"));
        medicoDAO.inserisciMedico(new Medico("MEDTEST02", "Giulia", "Verdi", "giulia@example.com", "medico", "pass456"));

        Paziente paziente = new Paziente(
                "Luca",
                "Bianchi",
                "PAZTEST01",
                "luca@example.com",
                "paziente",
                "M",
                "passpaziente",
                "Nessuna",
                LocalDate.of(1990, 1, 1),
                medicoDAO.getMedicoByCodiceFiscale("MEDTEST01"),
                "nessuno",
                "nessuna",
                "nessuna"
        );
        pazienteDAO.inserisciPaziente(paziente);
    }

    @Test
    void testInserisciMedico() throws Exception {
        // Provo a inserire un nuovo medico
        Medico nuovo = new Medico("MEDTEST03", "Anna", "Test", "anna@example.com", "medico", "test123");
        medicoDAO.inserisciMedico(nuovo);

        Medico recuperato = medicoDAO.getMedicoByCodiceFiscale("MEDTEST03");
        assertNotNull(recuperato);
        assertEquals("Anna", recuperato.getNome());

        medicoDAO.eliminaMedico("MEDTEST03");
    }

    @Test
    void testInserisciPaziente() throws Exception {
        // Provo a inserire un nuovo paziente
        Paziente paziente = pazienteDAO.findByCodiceFiscale("PAZTEST01");
        assertNotNull(paziente);
        assertEquals("Luca", paziente.getNome());
        assertEquals("MEDTEST01", paziente.getMedicoRiferimento().getCodiceFiscale());
    }

    @Test
    void testAggiornaMedicoCurantePaziente() throws Exception {
        // Provo ad aggiornare il medico curante di un paziente
        boolean aggiornato = pazienteDAO.aggiornaMedicoCurante("PAZTEST01", "MEDTEST02");
        assertTrue(aggiornato);

        Paziente aggiornatoDb = pazienteDAO.findByCodiceFiscale("PAZTEST01");
        assertNotNull(aggiornatoDb);
        assertEquals("MEDTEST02", aggiornatoDb.getMedicoRiferimento().getCodiceFiscale());
    }

    @Test
    void testEliminaPaziente() throws Exception {
        boolean eliminato = pazienteDAO.eliminaPaziente("PAZTEST01");
        assertTrue(eliminato);

        Paziente pazienteDb = pazienteDAO.findByCodiceFiscale("PAZTEST01");
        assertNull(pazienteDb);
    }

}
