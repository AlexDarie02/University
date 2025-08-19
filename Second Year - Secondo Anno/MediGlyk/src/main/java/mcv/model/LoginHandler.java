package mcv.model;

import diabetici.dao.MedicoDAO;
import diabetici.dao.UsersDAO;

import mcv.factory.ServiceFactory;

public class LoginHandler {
    private final ServiceFactory serviceFactory;
    private final MedicoDAO medicoDAO;
    private final UsersDAO usersDAO;

    public LoginHandler(ServiceFactory serviceFactory, MedicoDAO medicoDAO, UsersDAO usersDAO) {
        this.serviceFactory = serviceFactory;
        this.medicoDAO = medicoDAO;
        this.usersDAO = usersDAO;
    }

    public Utente verificaCredenziali(String codiceFiscale, String password, String ruolo) {
        try {
            switch (ruolo.toLowerCase()) {
                case "paziente":
                    PazienteManager pazienteManager = serviceFactory.getPazienteManager();
                    Paziente paziente = pazienteManager.getByCodiceFiscale(codiceFiscale);
                    if (paziente != null && paziente.getPassword().equals(password)) {
                        return paziente;
                    }
                    break;

                case "medico":
                    Medico medico = medicoDAO.getMedicoByCodiceFiscale(codiceFiscale);
                    if (medico != null && medico.getPassword().equals(password)) {
                        return medico;
                    }
                    break;

                default:
                    Utente utente = usersDAO.getUtenteByCredenziali(codiceFiscale, password, ruolo);
                    if (utente != null) {
                        return utente;
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
