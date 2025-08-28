package mcv.integration;

import diabetici.dao.DatabaseManager;
import diabetici.dao.PrescrizioneDAO;
import diabetici.dao.LogDAO;
import mcv.model.Prescrizione;
import mcv.model.PrescrizioneService;
import mcv.model.Medico;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrescrizioneIntegrationTest {

    private static Connection connection;
    private PrescrizioneDAO prescrizioneDAO;
    private LogDAO logDAO;
    private PrescrizioneService prescrizioneService;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DatabaseManager.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password)
                VALUES ('RRFF283NCJ22OS0P', 'Raffaelo', 'Gonzales', 'raff.gonz@example.com', 'medico', '444test')
            """);

            stmt.executeUpdate("""
                INSERT INTO pazienti (
                    codice_fiscale, nome, cognome, email, ruolo, password, genere,
                    data_nascita, ulter_info, codice_fiscale_medico,
                    fattori_rischio, patologie_pregresse, comorbidita
                ) VALUES (
                    'VRNGPP85M11F205X', 'Giuseppe', 'Verdi', 'giuseppe@example.com', 'paziente', 'testpass', 'M',
                    '1980-01-01', '', 'RRFF283NCJ22OS0P',
                    '', '', ''
                )
            """);
        }
    }

    @AfterAll
    static void cleanupPazientiEMedici() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM prescrizioni");
            stmt.executeUpdate("DELETE FROM log_azioni");
            stmt.executeUpdate("DELETE FROM pazienti WHERE codice_fiscale = 'VRNGPP85M11F205X'");
            stmt.executeUpdate("DELETE FROM medici WHERE codice_fiscale = 'RRFF283NCJ22OS0P'");
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() {
        prescrizioneDAO = new PrescrizioneDAO(connection);
        logDAO = new LogDAO(connection);
        prescrizioneService = new PrescrizioneService(prescrizioneDAO, logDAO);
    }

    @AfterEach
    void cleanup() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM prescrizioni");
            stmt.executeUpdate("DELETE FROM log_azioni");
        }
    }

    @Test
    void testAggiungiAndRecuperaPrescrizione() {
        // Creo un medico e un paziente per la prescrizione, e provo a inserire una prescrizion
        // per poi recuperarla
        Medico medico = new Medico("RRFF283NCJ22OS0P", "Raffaelo", "Gonzales", "raff.gonz@example.com", "medico", "444test");
        String cfPaziente = "VRNGPP85M11F205X";

        Prescrizione p = prescrizioneService.aggiungiPrescrizione(
                cfPaziente,
                "Insulina",
                "10",
                "2 volte al giorno",
                "Prima dei pasti",
                medico
        );

        assertNotNull(p);
        assertTrue(p.getId() > 0);
        assertEquals("Insulina", p.getNomeFarmaco());

        List<Prescrizione> prescrizioni = prescrizioneService.getPrescrizioniPerPaziente(cfPaziente);
        assertEquals(1, prescrizioni.size());
        assertEquals("Insulina", prescrizioni.get(0).getNomeFarmaco());
    }

    @Test
    void testAggiornaPrescrizione() {
        // Provo a modificare una prescrizione esistente
        Medico medico = new Medico("RRFF283NCJ22OS0P", "Raffaelo", "Gonzales", "raff.gonz@example.com", "medico", "444test");
        String cfPaziente = "VRNGPP85M11F205X";

        Prescrizione p = prescrizioneService.aggiungiPrescrizione(cfPaziente, "Farmaco1", "5", "1 volta", "Indicazioni", medico);

        p = new Prescrizione(p.getId(), "FarmacoModificato", "10", "2 volte", "Nuove indicazioni", p.getDataPrescrizione());

        boolean updated = prescrizioneService.aggiornaPrescrizione(p, cfPaziente, medico);
        assertTrue(updated);

        List<Prescrizione> prescrizioni = prescrizioneService.getPrescrizioniPerPaziente(cfPaziente);
        assertEquals(1, prescrizioni.size());
        assertEquals("FarmacoModificato", prescrizioni.get(0).getNomeFarmaco());
        assertEquals("10", prescrizioni.get(0).getDosaggio());
    }

    @Test
    void testEliminaPrescrizione() {
        Medico medico = new Medico("RRFF283NCJ22OS0P", "Raffaelo", "Gonzales", "raff.gonz@example.com", "medico", "444test");
        String cfPaziente = "VRNGPP85M11F205X";

        Prescrizione p = prescrizioneService.aggiungiPrescrizione(cfPaziente, "FarmacoToDelete", "5mg", "1 volta", "Indicazioni", medico);

        boolean deleted = prescrizioneService.eliminaPrescrizione(p.getId(), cfPaziente, medico);
        assertTrue(deleted);

        List<Prescrizione> prescrizioni = prescrizioneService.getPrescrizioniPerPaziente(cfPaziente);
        assertTrue(prescrizioni.isEmpty());
    }
}
