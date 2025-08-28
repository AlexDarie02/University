package mcv.model;

import diabetici.dao.RilevazioneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RilevazioneHandlerTest {

    private RilevazioneDAO daoMock;
    private RilevazioneHandler handler;

    @BeforeEach
    void setUp() {
        daoMock = mock(RilevazioneDAO.class);
        handler = new RilevazioneHandler(daoMock);
    }

    @Test
    void testGetRilevazioniSuccess() throws SQLException {
        // Verifico che il metodo ritorni la lista delle rilevazioni e che chiami il DAO
        List<Rilevazione> rilevazioni = List.of(
                new Rilevazione("CF123", 100, LocalDateTime.now(), "glicemia", "colazione")
        );
        when(daoMock.getRilevazioniByPeriodo("CF123", "ultimo_mese")).thenReturn(rilevazioni);

        List<Rilevazione> result = handler.getRilevazioni("CF123", "ultimo_mese");
        assertEquals(rilevazioni, result);
        verify(daoMock).getRilevazioniByPeriodo("CF123", "ultimo_mese");
    }

    @Test
    void testGetRilevazioniThrows() throws SQLException {
        // Verifico che il metodo per ottenere le rilevazioni lanci una SQLException se il DAO fallisce
        when(daoMock.getRilevazioniByPeriodo("CF123", "ultimo_mese")).thenThrow(new SQLException());

        assertThrows(SQLException.class, () -> handler.getRilevazioni("CF123", "ultimo_mese"));
    }
}
