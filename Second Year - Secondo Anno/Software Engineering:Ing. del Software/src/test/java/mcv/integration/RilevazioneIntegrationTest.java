package mcv.integration;

import diabetici.dao.RilevazioneDAO;
import mcv.model.Rilevazione;
import mcv.model.RilevazioneHandler;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RilevazioneIntegrationTest {

    private static Connection connection;
    private RilevazioneHandler handler;

    @BeforeAll
    static void setupDatabase() throws Exception {
        connection = diabetici.dao.DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "INSERT INTO pazienti (codice_fiscale, nome, cognome, email, password, genere, data_nascita, ruolo) " +
                            "VALUES ('PAZTEST01', 'Mario', 'Rossi', 'test@example.com', 'password123', 'M', '1980-01-01', 'paziente')"
            );

        }
    }

    @AfterAll
    static void cleanDatabase() throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM rilevazioni_glicemiche WHERE cf_paziente = 'PAZTEST01'");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'PAZTEST01'");
        }
        connection.close();
    }


    @BeforeEach
    void init() {
        RilevazioneDAO dao = new RilevazioneDAO(connection);
        handler = new RilevazioneHandler(dao);
    }

    @Test
    void testInserisciAndRecuperaRilevazione() throws Exception {
        // Provo a inserire una rilevazione per un paziente e poi a recuperarla
        LocalDateTime now = LocalDateTime.now();
        Rilevazione rilevazione = new Rilevazione("PAZTEST01", 120, now, "PREPASTO", "COLAZIONE");

        boolean inserted = handler.dao.insertRilevazione(rilevazione);
        assertTrue(inserted, "Inserimento rilevazione dovrebbe avere successo");


        List<Rilevazione> rilevazioni = handler.getRilevazioni("PAZTEST01", "ULTIMA_SETTIMANA");
        assertFalse(rilevazioni.isEmpty(), "La lista delle rilevazioni non deve essere vuota");

        Rilevazione trovata = rilevazioni.stream()
                .filter(r -> r.getTipo().equals("PREPASTO") && r.getTipoPasto().equals("COLAZIONE") && r.getValore() == 120)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Rilevazione inserita non trovata"));

        assertEquals("PAZTEST01", trovata.getCodiceFiscale());
        assertEquals("PREPASTO", trovata.getTipo());
        assertEquals("COLAZIONE", trovata.getTipoPasto());
        assertEquals(120, trovata.getValore());
        assertEquals(now.withNano(0), trovata.getDataOra().withNano(0)); // arrotondiamo nanosecondi
    }

    @Test
    void testGetUltimoPrePastoPerTipo() throws Exception {
        // Provo a recuperare l'ultima rilevazione pre-pasto per un tipo di pasto specifico
        LocalDate oggi = LocalDate.now();

        LocalDateTime oraPrePasto = LocalDateTime.of(oggi, java.time.LocalTime.of(11, 30));
        Rilevazione rilevazione = new Rilevazione("PAZTEST01", 110, oraPrePasto, "PREPASTO", "PRANZO");
        handler.dao.insertRilevazione(rilevazione);

        Rilevazione ultimoPrePasto = handler.dao.getUltimoPrePastoPerTipo("PAZTEST01", oggi, "PRANZO");
        assertNotNull(ultimoPrePasto, "L'ultimo pre-pasto per pranzo dovrebbe esistere");
        assertEquals("PREPASTO", ultimoPrePasto.getTipo());
        assertEquals("PRANZO", ultimoPrePasto.getTipoPasto());
        assertEquals(110, ultimoPrePasto.getValore());
    }
}
