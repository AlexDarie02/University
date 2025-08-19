package mcv.model;

import diabetici.dao.SintomoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 class SintomoManagerTest {

    private SintomoDAO mockDao;
    private SintomoManager sintomoManager;

    @BeforeEach
    public void setUp() {
        mockDao = Mockito.mock(SintomoDAO.class);
        sintomoManager = new SintomoManager(mockDao);
    }

    @Test
    public void testGetSintomiSegnalatiUltime24h() throws SQLException {
        // Verifico che il metodo ritorni la lista dei sintomi e che chiami il DAO
        String cf = "RSSMRA80A01H501U";
        List<String> sintomiAttesi = List.of("Febbre", "Tosse");

        when(mockDao.getSintomiSegnalatiUltime24h(cf)).thenReturn(sintomiAttesi);

        List<String> risultato = sintomoManager.getSintomiSegnalatiUltime24h(cf);

        assertEquals(sintomiAttesi, risultato);
        verify(mockDao, times(1)).getSintomiSegnalatiUltime24h(cf);
    }

    @Test
    public void testInserisciSintomo() throws SQLException {
        // Verifico che va a buon fine l'inserimento del sintomo e che chiami il DAO
        Sintomo s = new Sintomo("RSSMRA80A01H501U", "Nausea", LocalDateTime.now());

        sintomoManager.inserisciSintomo(s);

        verify(mockDao, times(1)).inserisciSintomo(s.getCfPaziente(), s.getSintomo());
    }
}
