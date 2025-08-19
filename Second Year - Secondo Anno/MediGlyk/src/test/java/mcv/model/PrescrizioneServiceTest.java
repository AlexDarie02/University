package mcv.model;

import diabetici.dao.LogDAO;
import diabetici.dao.PrescrizioneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrescrizioneServiceTest {

    private PrescrizioneDAO daoMock;
    private LogDAO logDAOMock;
    private PrescrizioneService service;
    private Medico medicoMock;

    @BeforeEach
    void setUp() {
        daoMock = mock(PrescrizioneDAO.class);
        logDAOMock = mock(LogDAO.class);
        service = new PrescrizioneService(daoMock, logDAOMock);
        medicoMock = mock(Medico.class);
        when(medicoMock.getCodiceFiscale()).thenReturn("CFMED");
    }

    @Test
    void testGetPrescrizioniPerPazienteSuccess() throws SQLException {
        // Verifico che il metodo ritorni la lista delle prescrizioni e che chiami il DAO
        when(daoMock.getPrescrizioniByPaziente("CFP")).thenReturn(List.of(mock(Prescrizione.class)));

        List<Prescrizione> result = service.getPrescrizioniPerPaziente("CFP");

        assertNotNull(result);
        verify(daoMock).getPrescrizioniByPaziente("CFP");
    }

    @Test
    void testAggiungiPrescrizioneSuccess() throws SQLException {
        // Creo una prescrizione e testa la sua aggiunta con inserimento del log
        Prescrizione prescrizioneCreata = new Prescrizione(1, "FarmacoX", "10mg", "2 volte al giorno", "dopo i pasti", LocalDate.now());
        when(daoMock.aggiungiPrescrizione(eq("CFP"), any(Prescrizione.class))).thenReturn(prescrizioneCreata);

        Prescrizione result = service.aggiungiPrescrizione("CFP", "FarmacoX", "10mg", "2 volte al giorno", "dopo i pasti", medicoMock);

        assertEquals(1, result.getId());
        verify(daoMock).aggiungiPrescrizione(eq("CFP"), any(Prescrizione.class));
        verify(logDAOMock).inserisciLog(any(LogAzione.class));
    }

    @Test
    void testAggiornaPrescrizioneSuccess() throws SQLException {
        // Controllo l'aggiornamento  della prescrizione e l'inserimento log se ha successo
        Prescrizione p = mock(Prescrizione.class);
        when(p.getId()).thenReturn(1);
        when(p.getNomeFarmaco()).thenReturn("FarmacoX");
        when(daoMock.aggiornaPrescrizione(p)).thenReturn(true);

        boolean result = service.aggiornaPrescrizione(p, "CFP", medicoMock);

        assertTrue(result);
        verify(daoMock).aggiornaPrescrizione(p);
        verify(logDAOMock).inserisciLog(any(LogAzione.class));
    }

    @Test
    void testAggiornaPrescrizioneFail() throws SQLException {
        // Verifico che non inserisca log se l'aggiornamento fallisce, quindi se ritorna false
        Prescrizione p = mock(Prescrizione.class);
        when(daoMock.aggiornaPrescrizione(p)).thenReturn(false);

        boolean result = service.aggiornaPrescrizione(p, "CFP", medicoMock);

        assertFalse(result);
        verify(logDAOMock, never()).inserisciLog(any());
    }

    @Test
    void testEliminaPrescrizioneSuccess() throws SQLException {
        // Verifico l'eliminazione e l'inserimento del log se ha successo
        when(daoMock.eliminaPrescrizione(1)).thenReturn(true);

        boolean result = service.eliminaPrescrizione(1, "CFP", medicoMock);

        assertTrue(result);
        verify(daoMock).eliminaPrescrizione(1);
        verify(logDAOMock).inserisciLog(any(LogAzione.class));
    }
}
