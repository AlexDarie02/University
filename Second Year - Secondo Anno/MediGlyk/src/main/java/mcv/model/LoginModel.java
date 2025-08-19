package mcv.model;

import diabetici.dao.MedicoDAO;
import diabetici.dao.UsersDAO;
import mcv.factory.ServiceFactory;

public class LoginModel {

    private final String codicefiscale;
    private final String password;
    private final String ruolo;
    private final LoginHandler loginHandler;

    public LoginModel(String codicefiscale, String password, String ruolo) {
        this(codicefiscale, password, ruolo,
                new LoginHandler(
                        ServiceFactory.getInstance(),
                        new MedicoDAO(ServiceFactory.getInstance().getConnection()),
                        new UsersDAO()
                )
        );
    }

    public LoginModel(String codicefiscale, String password, String ruolo, LoginHandler loginHandler) {
        this.codicefiscale = codicefiscale;
        this.password = password;
        this.ruolo = ruolo.toLowerCase();
        this.loginHandler = loginHandler;
    }

    public Utente checkLogin() {
        return loginHandler.verificaCredenziali(codicefiscale, password, ruolo);
    }
}

