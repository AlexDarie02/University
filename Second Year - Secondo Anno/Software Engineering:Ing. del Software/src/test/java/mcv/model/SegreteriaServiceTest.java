package mcv.model;

import diabetici.dao.MedicoDAO;
import diabetici.dao.PazienteDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.Mockito.*;

class SegreteriaServiceTest {

    private MedicoDAO medicoDAOMock;
    private PazienteDAO pazienteDAOMock;
    private SegreteriaService service;

    @BeforeEach
    void setUp() {
        medicoDAOMock = mock(MedicoDAO.class);
        pazienteDAOMock = mock(PazienteDAO.class);
        service = new SegreteriaService(medicoDAOMock, pazienteDAOMock);
    }

    @Test
    void testAggiungiMedico() throws SQLException {
        Medico medico = mock(Medico.class);
        service.aggiungiMedico(medico);
        verify(medicoDAOMock).inserisciMedico(medico);
    }

    @Test
    void testAggiungiPaziente() throws SQLException {
        Paziente paziente = mock(Paziente.class);
        service.aggiungiPaziente(paziente);
        verify(pazienteDAOMock).inserisciPaziente(paziente);
    }

    @Test
    void testGetMedicoByCodiceFiscale() throws SQLException {
        String cf = "CF123";
        service.getMedicoByCodiceFiscale(cf);
        verify(medicoDAOMock).getMedicoByCodiceFiscale(cf);
    }

    @Test
    void testEliminaMedico() throws SQLException {
        String cf = "CF123";
        service.eliminaMedico(cf);
        verify(medicoDAOMock).eliminaMedico(cf);
    }

    @Test
    void testEliminaPaziente() throws SQLException {
        String cf = "CF123";
        service.eliminaPaziente(cf);
        verify(pazienteDAOMock).eliminaPaziente(cf);
    }

}
