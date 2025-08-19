package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.NotificaDAO;
import mcv.factory.NotificaFactory;
import mcv.model.Notifica;
import mcv.model.NotificaHandler;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificaIntegrationTest {

    private static Connection connection;
    private NotificaHandler handler;

    @BeforeAll
    static void setupDatabase() throws Exception {
        connection = DatabaseManager.getConnection();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
                VALUES ('MEDTEST01', 'Giulio', 'Ferrari', 'giulio.ferrari@example.com', 'medico', 'medpass')
                ON DUPLICATE KEY UPDATE email = VALUES(email)
            """);

            stmt.executeUpdate("""
                INSERT INTO pazienti (
                    codice_fiscale, nome, cognome, email, ruolo, password, genere,
                    data_nascita, ulter_info, codice_fiscale_medico,
                    fattori_rischio, patologie_pregresse, comorbidita
                ) VALUES (
                    'PAZTEST01', 'Chiara', 'Bianchi', 'chiara.bianchi@example.com', 'paziente', 'pazpass', 'F',
                    '1990-06-15', '', 'MEDTEST01', '', '', ''
                )
                ON DUPLICATE KEY UPDATE email = VALUES(email)
            """);
        }
    }

    @AfterAll
    static void cleanDatabase() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM notifiche WHERE paziente_cf = 'PAZTEST01' OR codice_fiscale_medico = 'MEDTEST01'");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'PAZTEST01'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'MEDTEST01'");
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    @BeforeEach
    void init() {
        handler = new NotificaHandler(new NotificaDAO(connection));
    }

    @Test
    void testAggiuntaAndRicercaNotifica() throws Exception {
        // Provo a inserire una notifica per un paziente e poi a recuperarla
        Notifica notifica = NotificaFactory.creaTerapiaExtra("PAZTEST01", "Vitamina D", "MEDTEST01");
        handler.addNotifica(notifica);

        List<Notifica> result = handler.getNotificheByMedico("MEDTEST01");
        assertFalse(result.isEmpty());

        Notifica salvata = result.stream()
                .filter(n -> n.getPazienteCF().equals("PAZTEST01") && n.getMessaggio().contains("Vitamina D"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Notifica non trovata"));

        assertEquals("PAZTEST01", salvata.getPazienteCF());
        assertTrue(salvata.getMessaggio().contains("Vitamina D"));
        assertEquals("TERAPIA_EXTRA", salvata.getTipo());
        assertEquals("IN_ATTESA", salvata.getStatoRisposta());
    }

    @Test
    void testAggiornaStatoNotifica() throws Exception {
        // Provo a inserire una notifica, aggiornarne lo stato e verificare che sia stato salvato correttamente
        Notifica notifica = NotificaFactory.creaPromemoriaPazienteAssunzione("PAZTEST01", "MEDTEST01");
        handler.addNotifica(notifica);

        List<Notifica> result = handler.getNotificheByMedico("MEDTEST01");
        assertFalse(result.isEmpty());

        int id = result.get(0).getId();

        handler.rispondiNotifica(id, "ACCETTATA");

        List<Notifica> aggiornate = handler.getNotificheByMedico("MEDTEST01");
        assertEquals("ACCETTATA", aggiornate.get(0).getStatoRisposta());
    }

    @Test
    void testEliminaNotifichePerPaziente() throws Exception {
        Notifica notifica = NotificaFactory.creaGlicemiaGrave("PAZTEST01", 250, "digiuno", "colazione", "MEDTEST01");
        handler.addNotifica(notifica);

        handler.eliminaNotifichePerPaziente("PAZTEST01");

        List<Notifica> postDelete = handler.getNotificheByMedico("MEDTEST01");
        assertTrue(postDelete.stream().noneMatch(n -> n.getPazienteCF().equals("PAZTEST01")));
    }
}
