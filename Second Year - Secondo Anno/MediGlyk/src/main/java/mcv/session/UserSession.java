package mcv.session;

import mcv.model.Utente;

public class UserSession {
    // classe singleton per mantenere i dati dell'utente loggato

    private static UserSession instance;
    private Utente loggedInUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setLoggedInUser(Utente user) {
        this.loggedInUser = user;
    }

    public Utente getLoggedInUser() {
        return loggedInUser;
    }

    public void cleanUserSession() {
        loggedInUser = null;
    }

    public String getLoggedInUserCF() {
        return loggedInUser != null ? loggedInUser.getCodiceFiscale() : null;
    }

    public String getLoggedInUserRole() {
        return loggedInUser != null ? loggedInUser.getRuolo() : null;
    }
}