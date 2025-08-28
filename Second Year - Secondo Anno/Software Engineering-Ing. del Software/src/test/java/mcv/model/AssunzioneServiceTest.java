package mcv.model;

import diabetici.dao.AssunzioneDAO;
import mcv.factory.NotificaFactory;
import mcv.factory.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class AssunzioneServiceTest {

    private AssunzioneDAO daoMock;
    private AssunzioneService service;

    @BeforeEach
    void setUp() {
        daoMock = mock(AssunzioneDAO.class);
        service = new AssunzioneService(daoMock);
    }


    @Test
    void testSalvaAssunzione() {
        Assunzione a = new Assunzione("ABC123", "Paracetamolo", "500", LocalDateTime.now());

        service.salvaAssunzione(a);

        verify(daoMock).insertAssunzione(a);
    }

    @Test
    void testGetAssunzioniPerPaziente() {
        List<Assunzione> mockList = Collections.singletonList(new Assunzione("ABC123", "Ibuprofene", "200", LocalDateTime.now()));
        when(daoMock.getAssunzioniByPaziente("ABC123")).thenReturn(mockList);

        List<Assunzione> result = service.getAssunzioniPerPaziente("ABC123");

        assertEquals(1, result.size());
        assertEquals("Ibuprofene", result.get(0).getFarmaco());
    }

    @Test
    void testControllaInattivitaAssunzioni() {
        // Simulo un'assunzione avvenuta 4 giorni fa
        when(daoMock.getUltimaAssunzione("CF001")).thenReturn(LocalDateTime.now().minusDays(4));

        Medico fintoMedico = new Medico(
                "MED123",
                "Dott. Bianchi",
                "Rossi",
                "medico@example.com",
                "medico",
                "pass123"
        );

        Paziente fintoPaziente = new Paziente(
                "Mario",
                "Rossi",
                "CF001",
                "paziente@example.com",
                "paziente",
                "M",
                "pass123",
                null,
                LocalDate.of(1990, 1, 1),
                fintoMedico,
                "",
                "",
                ""
        );

        PazienteManager pazienteManagerMock = mock(PazienteManager.class);
        when(pazienteManagerMock.getByCodiceFiscale("CF001")).thenReturn(fintoPaziente);

        try (MockedStatic<ServiceFactory> sfMock = Mockito.mockStatic(ServiceFactory.class);
             MockedStatic<NotificaFactory> nfMock = Mockito.mockStatic(NotificaFactory.class);
             MockedStatic<NotificaHandler> nhMock = Mockito.mockStatic(NotificaHandler.class)) {

            ServiceFactory sfInstance = mock(ServiceFactory.class);
            sfMock.when(ServiceFactory::getInstance).thenReturn(sfInstance);
            when(sfInstance.getPazienteManager()).thenReturn(pazienteManagerMock);

            Notifica notificaMock = mock(Notifica.class);
            nfMock.when(() -> NotificaFactory.creaNessunaAssunzioneMedico("CF001", "MED123")).thenReturn(notificaMock);

            service.controllaInattivitaAssunzioni("CF001");

            // Verifico che la notifica sia stata inviata correttamente
            nhMock.verify(() -> NotificaHandler.inviaNotifica(notificaMock));
        }
    }


    @Test
    void testGetAssunzioniInsulina() {

        when(daoMock.getAssunzioniInsulinaByPeriodo(eq("CF001"), any(), any()))
                .thenReturn(Collections.emptyList());

        List<Assunzione> result = service.getAssunzioniInsulina("CF001", "ULTIMO_MESE");

        assertNotNull(result);
        verify(daoMock).getAssunzioniInsulinaByPeriodo(eq("CF001"), any(), any());
    }
}
