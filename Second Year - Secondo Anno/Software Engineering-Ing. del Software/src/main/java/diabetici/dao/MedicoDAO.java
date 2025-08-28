package diabetici.dao;

import mcv.model.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {
    private final Connection conn;

    public MedicoDAO(Connection conn) {
        this.conn = conn;
    }

    public Medico getMedicoByCodiceFiscale(String codiceFiscale){
        String sql = "SELECT * FROM medici WHERE codice_fiscale = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void inserisciMedico(Medico m) throws SQLException {
        String sql = "INSERT INTO medici (codice_fiscale, nome, cognome, email, ruolo, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getCodiceFiscale());
            ps.setString(2, m.getNome());
            ps.setString(3, m.getCognome());
            ps.setString(4, m.getEmail());
            ps.setString(5, m.getRuolo());
            ps.setString(6, m.getPassword());
            ps.executeUpdate();
        }
    }

    public List<String> suggerisciCodiciFiscali(String prefix) {
        List<String> risultati = new ArrayList<>();
        String query = "SELECT codice_fiscale FROM medici WHERE codice_fiscale LIKE ? LIMIT 10";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                risultati.add(rs.getString("codice_fiscale"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return risultati;
    }

    public boolean eliminaMedico(String codiceFiscale) {
        String query = "DELETE FROM medici WHERE codice_fiscale = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, codiceFiscale);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}