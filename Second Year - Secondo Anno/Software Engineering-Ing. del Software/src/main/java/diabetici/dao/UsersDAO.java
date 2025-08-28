package diabetici.dao;

import mcv.model.Medico;
import mcv.model.Paziente;
import mcv.model.Segreteria;
import mcv.model.Utente;

import java.sql.*;

public class UsersDAO {

    public Utente getUtenteByCredenziali(String codiceFiscale, String password, String ruolo) {
        String query;
        switch (ruolo.toLowerCase()) {
            case "medico":
                query = "SELECT * FROM medici WHERE codice_fiscale = ? AND password = ?";
                break;
            case "paziente":
                query = "SELECT * FROM pazienti WHERE codice_fiscale = ? AND password = ?";
                break;
            case "segreteria":
                query = "SELECT * FROM segreteria WHERE codice_fiscale = ? AND password = ?";
                break;
            default:
                return null;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codiceFiscale);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    switch (ruolo.toLowerCase()) {
                        case "medico":
                            return new Medico(
                                    rs.getString("nome"),
                                    rs.getString("cognome"),
                                    rs.getString("codice_fiscale"),
                                    rs.getString("email"),
                                    rs.getString("ruolo"),
                                    rs.getString("password")
                                    
                            );

                        case "segreteria":
                            return new Segreteria(
                                    rs.getString("codice_fiscale"),
                                    rs.getString("email"),
                                    rs.getString("ruolo"),
                                    rs.getString("password")
                            );

                        case "paziente":
                            String medicoCf = rs.getString("codice_fiscale_medico");
                            Medico medico = getMedicoByCodiceFiscale(medicoCf);

                            String fattoriRischio = rs.getString("fattori_rischio");
                            String patologiePregresse = rs.getString("patologie_pregresse");
                            String comorbidita = rs.getString("comorbidita");

                            return new Paziente(
                                    rs.getString("nome"),
                                    rs.getString("cognome"),
                                    rs.getString("codice_fiscale"),
                                    rs.getString("email"),
                                    rs.getString("ruolo"),
                                    rs.getString("genere"),
                                    rs.getString("password"),
                                    rs.getString("ulter_info"),
                                    rs.getDate("data_nascita").toLocalDate(),
                                    medico,
                                    fattoriRischio,
                                    patologiePregresse,
                                    comorbidita
                            );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Medico getMedicoByCodiceFiscale(String codiceFiscale) throws SQLException {
        String query = "SELECT * FROM medici WHERE codice_fiscale = ? AND ruolo = 'medico'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codiceFiscale);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Medico(
                        rs.getString("codice_fiscale"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("email"),
                        rs.getString("ruolo"),
                        rs.getString("password")
                );
            }
        }

        return null;
    }

}
