package mcv.model;

import diabetici.dao.LogDAO;
import diabetici.dao.PazienteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class PazienteManagerTest {

    private PazienteDAO pazienteDAOMock;
    private LogDAO logDAOMock;
    private PazienteManager manager;

    @BeforeEach
    void setUp() {
        pazienteDAOMock = mock(PazienteDAO.class);
        logDAOMock = mock(LogDAO.class);
        manager = new PazienteManager(pazienteDAOMock, logDAOMock);
    }

    @Test
    void testGetAllPazientiSuccess() throws SQLException {
        // Provo a prendere dal DB tutti i pazienti
        List<Paziente> pazienti = Arrays.asList(
                mock(Paziente.class),
                mock(Paziente.class)
        );
        when(pazienteDAOMock.findAll()).thenReturn(pazienti);

        List<Paziente> result = manager.getAllPazienti();
        assertEquals(2, result.size());
        verify(pazienteDAOMock).findAll();
    }

    @Test
    void testGetByCodiceFiscaleSuccess() throws SQLException {
        // Provo a prendere dal DB un certo paziente per CF
        Paziente p = mock(Paziente.class);
        when(pazienteDAOMock.findByCodiceFiscale("CFP")).thenReturn(p);

        Paziente result = manager.getByCodiceFiscale("CFP");
        assertEquals(p, result);
    }

    @Test
    void testAggiornaInformazioniSanitarieSuccess() throws SQLException {
        // Testo se va a buon fine l'aggiornamento delle informazioni di un paziente
        Medico medico = mock(Medico.class);
        when(medico.getCodiceFiscale()).thenReturn("CFMED");
        when(pazienteDAOMock.aggiornaInfoSanitarie("CFP", "fattori", "patologie", "comorbidita")).thenReturn(true);

        boolean result = manager.aggiornaInformazioniSanitarie("CFP", "fattori", "patologie", "comorbidita", medico);
        assertTrue(result);

        verify(pazienteDAOMock).aggiornaInfoSanitarie("CFP", "fattori", "patologie", "comorbidita");
        verify(logDAOMock).inserisciLog(any(LogAzione.class));
    }

    @Test
    void testAggiornaInformazioniSanitarieFail() throws SQLException {
        // Verifico che in caso di fallimento non si crei nessun log
        Medico medico = mock(Medico.class);
        when(pazienteDAOMock.aggiornaInfoSanitarie("CFP", "fattori", "patologie", "comorbidita")).thenReturn(false);

        boolean result = manager.aggiornaInformazioniSanitarie("CFP", "fattori", "patologie", "comorbidita", medico);
        assertFalse(result);

        verify(pazienteDAOMock).aggiornaInfoSanitarie("CFP", "fattori", "patologie", "comorbidita");
        verify(logDAOMock, never()).inserisciLog(any());
    }

    @Test
    void testAggiornaInfoAggiuntiveSuccess() throws SQLException {
        when(pazienteDAOMock.aggiungiInfoAggiuntive("CFP", "info")).thenReturn(true);
        boolean result = manager.aggiornaInfoAggiuntive("CFP", "info");
        assertTrue(result);
    }

    @Test
    void testSetUlterInfoSuccess() throws SQLException {
        when(pazienteDAOMock.setUlterInfo("CFP", "info")).thenReturn(true);
        boolean result = manager.setUlterInfo("CFP", "info");
        assertTrue(result);
    }
}
