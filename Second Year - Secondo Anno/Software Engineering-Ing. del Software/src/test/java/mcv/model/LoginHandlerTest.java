package mcv.model;

import diabetici.dao.MedicoDAO;
import diabetici.dao.UsersDAO;
import mcv.factory.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginHandlerTest {

    private ServiceFactory serviceFactoryMock;
    private MedicoDAO medicoDAOMock;
    private UsersDAO usersDAOMock;
    private LoginHandler loginHandler;

    @BeforeEach
    void setUp() {
        serviceFactoryMock = mock(ServiceFactory.class);
        medicoDAOMock = mock(MedicoDAO.class);
        usersDAOMock = mock(UsersDAO.class);

        loginHandler = new LoginHandler(serviceFactoryMock, medicoDAOMock, usersDAOMock);
    }


    // Provo per ogni tipologia di utente se il login funziona correttamente
    @Test
    void testLoginPazienteSuccess() {
        PazienteManager pazienteManagerMock = mock(PazienteManager.class);
        Paziente pazienteMock = mock(Paziente.class);

        when(serviceFactoryMock.getPazienteManager()).thenReturn(pazienteManagerMock);

        when(pazienteManagerMock.getByCodiceFiscale("CF123")).thenReturn(pazienteMock);

        when(pazienteMock.getPassword()).thenReturn("pass");

        Utente result = loginHandler.verificaCredenziali("CF123", "pass", "paziente");

        assertNotNull(result);
        assertEquals(pazienteMock, result);
    }

    @Test
    void testLoginMedicoSuccess() {
        Medico medicoMock = mock(Medico.class);

        when(medicoDAOMock.getMedicoByCodiceFiscale("CF456")).thenReturn(medicoMock);

        when(medicoMock.getPassword()).thenReturn("secure");

        Utente result = loginHandler.verificaCredenziali("CF456", "secure", "medico");

        assertNotNull(result);
        assertEquals(medicoMock, result);
    }

    @Test
    void testLoginSegreteriaSuccess() {
        Utente utenteMock = mock(Utente.class);

        when(usersDAOMock.getUtenteByCredenziali("CF789", "pwd", "altro")).thenReturn(utenteMock);

        Utente result = loginHandler.verificaCredenziali("CF789", "pwd", "altro");

        assertNotNull(result);
        assertEquals(utenteMock, result);
    }

    @Test
    void testLoginFallito() {
        PazienteManager pazienteManagerMock = mock(PazienteManager.class);

        when(serviceFactoryMock.getPazienteManager()).thenReturn(pazienteManagerMock);
        when(pazienteManagerMock.getByCodiceFiscale("CF000")).thenReturn(null);

        Utente result = loginHandler.verificaCredenziali("CF000", "wrong", "paziente");

        assertNull(result);
    }
}
