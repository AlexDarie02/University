package mcv.model;

import diabetici.dao.NotificaDAO;
import mcv.session.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificaHandlerTest {

    private NotificaDAO daoMock;
    private NotificaHandler handler;

    @BeforeEach
    void setUp() {
        daoMock = mock(NotificaDAO.class);
        handler = new NotificaHandler(daoMock);
    }

    @Test
    void testAddNotifica() throws Exception {
        // Provo ad aggiungere una nuova notifica al DB
        Notifica notifica = new Notifica("CFPZ", "Test msg", LocalDateTime.now(), "ALTA", "CFMED");

        handler.addNotifica(notifica);

        verify(daoMock).save(notifica);
    }

    @Test
    void testGetAllNotifiche() throws Exception {
        // Provo a recuperare tutte le notifiche dal DB
        List<Notifica> lista = Arrays.asList(
                new Notifica("CF1", "Msg1", LocalDateTime.now(), "MODERATA", "CFM1"),
                new Notifica("CF2", "Msg2", LocalDateTime.now(), "ALTA", "CFM2")
        );
        when(daoMock.findAll()).thenReturn(lista);

        List<Notifica> result = handler.getAllNotifiche();

        assertEquals(2, result.size());
        assertEquals("CF1", result.get(0).getPazienteCF());
    }

    @Test
    void testGetNotificheByMedico() throws Exception {
        // Provo a recuperare le notifiche per un medico specifico, per CF
        List<Notifica> expected = Arrays.asList(
                new Notifica("CF1", "Msg", LocalDateTime.now(), "TIPO", "CFMED")
        );
        when(daoMock.findNotificheByMedico("CFMED")).thenReturn(expected);

        List<Notifica> result = handler.getNotificheByMedico("CFMED");

        assertEquals(expected, result);
    }

    @Test
    void testRispondiNotifica() throws Exception {
        handler.rispondiNotifica(10, "COMPATIBILE");
        verify(daoMock).aggiornaStato(10, "COMPATIBILE");
    }

    @Test
    void testEliminaNotifichePerMedico() {
        // Provo a eliminare le notifiche per un medico specifico
        try (MockedStatic<UserSession> sessionMock = Mockito.mockStatic(UserSession.class)) {
            UserSession mockSession = mock(UserSession.class);
            sessionMock.when(UserSession::getInstance).thenReturn(mockSession);
            when(mockSession.getLoggedInUserCF()).thenReturn("CFMED");

            handler.eliminaNotifichePerMedico();

            verify(daoMock).eliminaNotificheMedico("CFMED");
        }
    }

    @Test
    void testEliminaNotifichePerPaziente() throws Exception {
        handler.eliminaNotifichePerPaziente("CFPZ");
        verify(daoMock).eliminaNotifichePerPaziente("CFPZ");
    }


}
