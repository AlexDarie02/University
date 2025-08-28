package diabetici.dao;

import mcv.model.Notifica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificaDAO {
    private final Connection conn;
    public NotificaDAO(Connection conn) {
        this.conn = conn;
    }

    public void save(Notifica n) throws SQLException {
        String sql = "INSERT INTO notifiche (paziente_cf, messaggio, timestamp, tipo, stato_risposta, codice_fiscale_medico) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getPazienteCF());
            ps.setString(2, n.getMessaggio());
            ps.setTimestamp(3, Timestamp.valueOf(n.getTimestamp()));
            ps.setString(4, n.getTipo());
            ps.setString(5, n.getStatoRisposta());
            ps.setString(6, n.getMedicoCF());
            ps.executeUpdate();
        }
    }

    public List<Notifica> findAll() throws SQLException {
        String sql = "SELECT id, paziente_cf, messaggio, timestamp, tipo, stato_risposta, codice_fiscale_medico FROM notifiche ORDER BY timestamp DESC";
        List<Notifica> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notifica(
                        rs.getInt("id"),
                        rs.getString("paziente_cf"),
                        rs.getString("messaggio"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getString("tipo"),
                        rs.getString("stato_risposta"),
                        rs.getString("codice_fiscale_medico")
                ));
            }
        }
        return list;
    }

    public void aggiornaStato(int id, String nuovoStato) throws SQLException {
        String sql = "UPDATE notifiche SET stato_risposta = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoStato);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Notifica> findNotificheByMedico(String medicoCF) throws SQLException {
        String sql = "SELECT n.id, n.paziente_cf, n.messaggio, n.timestamp, n.tipo, n.stato_risposta, n.codice_fiscale_medico " +
                "FROM notifiche n " +
                "JOIN pazienti p ON n.paziente_cf = p.codice_fiscale " +
                "WHERE p.codice_fiscale_medico = ? " +
                "ORDER BY n.timestamp DESC";

        List<Notifica> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicoCF);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Notifica(
                            rs.getInt("id"),
                            rs.getString("paziente_cf"),
                            rs.getString("messaggio"),
                            rs.getTimestamp("timestamp").toLocalDateTime(),
                            rs.getString("tipo"),
                            rs.getString("stato_risposta"),
                            rs.getString("codice_fiscale_medico")
                    ));
                }
            }
        }
        return lista;
    }

    public void eliminaNotificheMedico(String codiceMedico) {
        String sql = "DELETE FROM notifiche WHERE codice_fiscale_medico = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceMedico);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante l'eliminazione delle notifiche", e);
        }
    }

    public void eliminaNotifichePerPaziente(String codiceFiscalePaziente) throws SQLException {
        String sql = "DELETE FROM notifiche WHERE paziente_cf = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscalePaziente);
            ps.executeUpdate();
        }
    }
}
