package mcv.model;

import diabetici.dao.RilevazioneDAO;
import diabetici.dao.SegnalazioneGlicemiaDAO;
import mcv.factory.NotificaFactory;
import mcv.session.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckRilevazioneTest {

    RilevazioneDAO rilevazioneDAOMock;
    SegnalazioneGlicemiaDAO segnalazioneDAOMock;
    CheckRilevazione check;

    @BeforeEach
    void setUp() {
        rilevazioneDAOMock = mock(RilevazioneDAO.class);
        segnalazioneDAOMock = mock(SegnalazioneGlicemiaDAO.class);
        check = new CheckRilevazione(rilevazioneDAOMock, segnalazioneDAOMock);
    }

    @Test
    void testIsPrePastoValido() {
        assertTrue(check.isPrePastoValido(80));
        assertTrue(check.isPrePastoValido(130));
        assertFalse(check.isPrePastoValido(79));
        assertFalse(check.isPrePastoValido(131));
    }

    @Test
    void testIsPostPastoValido() {
        assertTrue(check.isPostPastoValido(180));
        assertTrue(check.isPostPastoValido(50));
        assertFalse(check.isPostPastoValido(181));
    }


    @Test
    void testInviaSegnalazioneMedico() throws SQLException {
        // Provo se viene chiamato il metodo per inserire una segnalazione, in caso sia necessario
        Paziente fakePaziente = mock(Paziente.class);
        Medico fakeMedico = mock(Medico.class);
        when(fakeMedico.getCodiceFiscale()).thenReturn("CFMED");
        when(fakePaziente.getMedicoRiferimento()).thenReturn(fakeMedico);

        try (MockedStatic<UserSession> userSessionMock = Mockito.mockStatic(UserSession.class)) {
            UserSession sessionMock = mock(UserSession.class);
            userSessionMock.when(UserSession::getInstance).thenReturn(sessionMock);
            when(sessionMock.getLoggedInUser()).thenReturn(fakePaziente);

            check.inviaSegnalazioneMedico("CFPZ", 200, "PREPASTO", LocalDateTime.now());

            verify(segnalazioneDAOMock).inserisciSegnalazione(any(), eq("CFMED"));
        }
    }

    // Stesso ragionamento di prova per notifica glicemia media
    @Test
    void testInsertRilevazione() throws SQLException {
        Paziente fakePaziente = mock(Paziente.class);
        Medico fakeMedico = mock(Medico.class);
        when(fakeMedico.getCodiceFiscale()).thenReturn("CFMED");
        when(fakePaziente.getMedicoRiferimento()).thenReturn(fakeMedico);

        try (MockedStatic<UserSession> userSessionMock = Mockito.mockStatic(UserSession.class);
             MockedStatic<NotificaHandler> nhMock = Mockito.mockStatic(NotificaHandler.class);
             MockedStatic<NotificaFactory> nfMock = Mockito.mockStatic(NotificaFactory.class)) {

            UserSession sessionMock = mock(UserSession.class);
            userSessionMock.when(UserSession::getInstance).thenReturn(sessionMock);
            when(sessionMock.getLoggedInUser()).thenReturn(fakePaziente);

            Rilevazione rilevazioneMock = mock(Rilevazione.class);
            when(rilevazioneMock.getTipo()).thenReturn("PREPASTO");
            when(rilevazioneMock.getValore()).thenReturn(170);
            when(rilevazioneMock.getCodiceFiscale()).thenReturn("CFPZ");
            when(rilevazioneMock.getDataOra()).thenReturn(LocalDateTime.now());
            when(rilevazioneMock.getTipoPasto()).thenReturn("PRANZO");

            Notifica fakeNotifica = mock(Notifica.class);
            nfMock.when(() ->
                    NotificaFactory.creaGlicemiaGrave("CFPZ", 170, "PREPASTO", "PRANZO", "CFMED")
            ).thenReturn(fakeNotifica);

            when(rilevazioneDAOMock.insertRilevazione(rilevazioneMock)).thenReturn(true);

            boolean result = check.insertRilevazione(rilevazioneMock);

            assertFalse(result);

            nhMock.verify(() -> NotificaHandler.inviaNotifica(fakeNotifica));
        }
    }

    @Test
    void testControllaOraPostPasto() {
        LocalDateTime pre = LocalDateTime.now();
        LocalDateTime post = pre.plusHours(3);
        assertTrue(check.controllaOraPostPasto(pre, post));
        assertFalse(check.controllaOraPostPasto(pre, pre.plusMinutes(90)));
    }


    @Test
    void testGetUltimoPrePastoSePresente_success() throws SQLException {
        // Provo se il metodo ritorna correttamente una rilevazione pre-pasto se presente
        Rilevazione mockRilevazione = mock(Rilevazione.class);
        when(rilevazioneDAOMock.getUltimoPrePastoPerTipo("CFPZ", LocalDate.now(), "PRANZO"))
                .thenReturn(mockRilevazione);

        Rilevazione result = check.getUltimoPrePastoSePresente("CFPZ", LocalDate.now(), "PRANZO");

        assertEquals(mockRilevazione, result);
    }

    @Test
    void testGetUltimoPrePastoSePresente_exception() throws SQLException {
        // Provo il comportamento in caso di eccezione durante il recupero
        when(rilevazioneDAOMock.getUltimoPrePastoPerTipo(anyString(), any(), any()))
                .thenThrow(new SQLException("DB error"));

        Rilevazione result = check.getUltimoPrePastoSePresente("CFPZ", LocalDate.now(), "PRANZO");

        assertNull(result);
    }
}
