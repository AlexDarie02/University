package diabetici.dao;

import mcv.model.Prescrizione;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescrizioneDAO {
    private final Connection conn;

    public PrescrizioneDAO(Connection connection) {
        this.conn = connection;
    }

    public List<Prescrizione> getPrescrizioniByPaziente(String codiceFiscale) throws SQLException {
        List<Prescrizione> prescrizioni = new ArrayList<>();
        String query = "SELECT id, nome_farmaco, dosaggio, frequenza, indicazioni, data_prescrizione " +
                "FROM prescrizioni WHERE paziente_cf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, codiceFiscale);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                prescrizioni.add(new Prescrizione(
                        rs.getInt("id"),
                        rs.getString("nome_farmaco"),
                        rs.getString("dosaggio"),
                        rs.getString("frequenza"),
                        rs.getString("indicazioni"),
                        rs.getObject("data_prescrizione", LocalDate.class)
                ));
            }
        }
        return prescrizioni;
    }

    public Prescrizione aggiungiPrescrizione(String codiceFiscale, Prescrizione prescrizione) throws SQLException {
        String query = "INSERT INTO prescrizioni (paziente_cf, nome_farmaco, dosaggio, frequenza, indicazioni, data_prescrizione) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, codiceFiscale);
            stmt.setString(2, prescrizione.getNomeFarmaco());
            stmt.setString(3, prescrizione.getDosaggio());
            stmt.setString(4, prescrizione.getFrequenza());
            stmt.setString(5, prescrizione.getIndicazioni());
            stmt.setDate(6, Date.valueOf(prescrizione.getDataPrescrizione()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Nessuna riga inserita.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Prescrizione(
                            rs.getInt(1),
                            prescrizione.getNomeFarmaco(),
                            prescrizione.getDosaggio(),
                            prescrizione.getFrequenza(),
                            prescrizione.getIndicazioni(),
                            prescrizione.getDataPrescrizione()
                    );
                } else {
                    throw new SQLException("Nessun ID generato.");
                }
            }
        }
    }

    public boolean aggiornaPrescrizione(Prescrizione p) throws SQLException {
        String query = "UPDATE prescrizioni SET nome_farmaco=?, dosaggio=?, frequenza=?, indicazioni=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, p.getNomeFarmaco());
            stmt.setString(2, p.getDosaggio());
            stmt.setString(3, p.getFrequenza());
            stmt.setString(4, p.getIndicazioni());
            stmt.setInt(5, p.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminaPrescrizione(int id) throws SQLException {
        String query = "DELETE FROM prescrizioni WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

}
