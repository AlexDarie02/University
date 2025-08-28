package mcv.model;

import diabetici.dao.MedicoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicoManagerTest {

    private MedicoDAO medicoDAOMock;
    private MedicoManager manager;

    @BeforeEach
    void setUp() {
        medicoDAOMock = mock(MedicoDAO.class);
        manager = new MedicoManager(medicoDAOMock);
    }

    @Test
    void testSuggerisciCodiciFiscali() {
        // Provo se il metodo suggerisce correttamente i codici fiscali
        String prefix = "ABC";
        List<String> codiciAttesi = Arrays.asList("ABC123", "ABC456");
        when(medicoDAOMock.suggerisciCodiciFiscali(prefix)).thenReturn(codiciAttesi);

        List<String> result = manager.suggerisciCodiciFiscali(prefix);

        assertEquals(codiciAttesi, result);
        verify(medicoDAOMock).suggerisciCodiciFiscali(prefix);
    }
}
