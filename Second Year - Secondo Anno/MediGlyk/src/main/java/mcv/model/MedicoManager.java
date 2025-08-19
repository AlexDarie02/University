package mcv.model;

import diabetici.dao.MedicoDAO;
import java.util.List;

public class MedicoManager {
    private final MedicoDAO medicoDAO;

    public MedicoManager(MedicoDAO medicoDAO) {
        this.medicoDAO = medicoDAO;
    }

    public List<String> suggerisciCodiciFiscali(String prefix) {
        return medicoDAO.suggerisciCodiciFiscali(prefix);
    }
}
