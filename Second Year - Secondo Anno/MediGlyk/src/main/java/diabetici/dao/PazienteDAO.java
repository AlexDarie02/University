package diabetici.dao;

import mcv.model.Medico;
import mcv.model.Paziente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PazienteDAO {
    private final Connection conn;

    public PazienteDAO(Connection conn) {
        this.conn = conn;
    }

    public List<Paziente> findAll() throws SQLException {
        String sql = "SELECT p.*, " +
                "m.nome AS m_nome, m.cognome AS m_cognome, m.email AS m_email, " +
                "m.codice_fiscale AS m_codice_fiscale, m.ruolo AS m_ruolo " +
                "FROM pazienti p " +
                "JOIN medici m ON p.codice_fiscale_medico = m.codice_fiscale";

        List<Paziente> lista = new ArrayList<>();
        MedicoDAO medicoDAO = new MedicoDAO(conn);


        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Medico medico = medicoDAO.getMedicoByCodiceFiscale(rs.getString("codice_fiscale_medico"));
                Paziente p = new Paziente(
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
                        rs.getString("fattori_rischio"),
                        rs.getString("patologie_pregresse"),
                        rs.getString("comorbidita")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    public Paziente findByCodiceFiscale(String cf) throws SQLException {
        String sql = "SELECT * FROM pazienti WHERE codice_fiscale = ?";
        MedicoDAO medicoDAO = new MedicoDAO(conn);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Medico medico = medicoDAO.getMedicoByCodiceFiscale(rs.getString("codice_fiscale_medico"));

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
                            rs.getString("fattori_rischio"),
                            rs.getString("patologie_pregresse"),
                            rs.getString("comorbidita")
                    );
                }
            }
        }
        return null;
    }

    public boolean aggiornaInfoSanitarie(String codiceFiscale, String fattoriRischio, String patologiePregresse, String comorbidita) throws SQLException {
        String sql = "UPDATE pazienti SET fattori_rischio = ?, patologie_pregresse = ?, comorbidita = ? WHERE codice_fiscale = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fattoriRischio);
            ps.setString(2, patologiePregresse);
            ps.setString(3, comorbidita);
            ps.setString(4, codiceFiscale);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean aggiungiInfoAggiuntive(String codiceFiscale, String ulterInfo) throws SQLException {
        String query = "UPDATE pazienti SET ulter_info = CONCAT(COALESCE(ulter_info,''), ?) WHERE codice_fiscale = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "\n" + ulterInfo);
            stmt.setString(2, codiceFiscale);
            return stmt.executeUpdate() > 0;
        }
    }


    public boolean inserisciPaziente(Paziente p) throws SQLException {
        String sql = "INSERT INTO pazienti (codice_fiscale, nome, cognome, email, ruolo, password, genere, data_nascita, ulter_info, codice_fiscale_medico, fattori_rischio, patologie_pregresse, comorbidita) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getCodiceFiscale());
            stmt.setString(2, p.getNome());
            stmt.setString(3, p.getCognome());
            stmt.setString(4, p.getEmail());
            stmt.setString(5, p.getRuolo());
            stmt.setString(6, p.getPassword());
            stmt.setString(7, p.getGenere());
            stmt.setDate(8, Date.valueOf(p.getDataNascita()));
            stmt.setString(9, p.getUlter_info());
            stmt.setString(10, p.getMedicoRiferimento().getCodiceFiscale());
            stmt.setString(11, p.getFattoriRischio());
            stmt.setString(12, p.getPatologiePregresse());
            stmt.setString(13, p.getComorbidita());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminaPaziente(String codiceFiscale) throws SQLException {
        String sql = "DELETE FROM pazienti WHERE codice_fiscale = ? AND ruolo = 'paziente'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<String> suggerisciCodiciFiscali(String prefix) {
        List<String> risultati = new ArrayList<>();
        String query = "SELECT codice_fiscale FROM pazienti WHERE codice_fiscale LIKE ? LIMIT 10";

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

    public boolean aggiornaMedicoCurante(String cfPaziente, String cfMedico) throws SQLException {
        String sql = "UPDATE pazienti SET codice_fiscale_medico = ? WHERE codice_fiscale = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cfMedico);
            stmt.setString(2, cfPaziente);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean setUlterInfo(String codiceFiscale, String nuovoUlterInfo) throws SQLException {
        String query = "UPDATE pazienti SET ulter_info = ? WHERE codice_fiscale = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nuovoUlterInfo);
            stmt.setString(2, codiceFiscale);
            return stmt.executeUpdate() > 0;
        }
    }






}
