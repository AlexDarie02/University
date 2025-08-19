package diabetici.dao;

import mcv.model.Rilevazione;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RilevazioneDAO {
    private final Connection connection;

    public RilevazioneDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertRilevazione(Rilevazione rilevazione) throws SQLException {
        String query = "INSERT INTO rilevazioni_glicemiche (cf_paziente, valore, data_ora, tipo, tipo_pasto) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, rilevazione.getCodiceFiscale());
            stmt.setInt(2, rilevazione.getValore());
            stmt.setTimestamp(3, Timestamp.valueOf(rilevazione.getDataOra()));
            stmt.setString(4, rilevazione.getTipo());
            stmt.setString(5, rilevazione.getTipoPasto());

            boolean success = stmt.executeUpdate() > 0;

            return success;
        }
    }

    public List<Rilevazione> getRilevazioniByPaziente(String cfPaziente) throws SQLException {
        String query = "SELECT valore, tipo, data_ora, tipo_pasto FROM rilevazioni_glicemiche WHERE cf_paziente = ? ORDER BY data_ora DESC";
        List<Rilevazione> rilevazioni_glicemiche = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rilevazioni_glicemiche.add(new Rilevazione(
                            cfPaziente,
                            rs.getInt("valore"),
                            rs.getTimestamp("data_ora").toLocalDateTime(),
                            rs.getString("tipo"),
                            rs.getString("tipo_pasto")
                    ));
                }
            }
        }
        return rilevazioni_glicemiche;
    }

    public Rilevazione getUltimoPrePastoPerTipo(String cfPaziente, LocalDate giorno, String tipoPasto) throws SQLException {
        String query = """
            SELECT * FROM rilevazioni_glicemiche 
            WHERE cf_paziente = ? 
              AND tipo = 'PREPASTO' 
              AND tipo_pasto = ? 
              AND DATE(data_ora) = ? 
            ORDER BY data_ora DESC 
            LIMIT 1
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            stmt.setString(2, tipoPasto);
            stmt.setDate(3, java.sql.Date.valueOf(giorno));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Rilevazione(
                        rs.getString("cf_paziente"),
                        rs.getInt("valore"),
                        rs.getTimestamp("data_ora").toLocalDateTime(),
                        rs.getString("tipo"),
                        rs.getString("tipo_pasto")
                );
            }
        }

        return null;
    }

    public List<Rilevazione> getRilevazioniByPeriodo(String cfPaziente, String periodo) throws SQLException {
        String query = "SELECT * FROM rilevazioni_glicemiche WHERE cf_paziente = ? ";

        switch (periodo) {
            case "ULTIMA_SETTIMANA" -> query += "AND data_ora >= NOW() - INTERVAL 7 DAY";
            case "ULTIMO_MESE"      -> query += "AND data_ora >= NOW() - INTERVAL 1 MONTH";
            case "ULTIMI_3_MESI"    -> query += "AND data_ora >= NOW() - INTERVAL 3 MONTH";
        }

        query += " ORDER BY data_ora ASC";

        List<Rilevazione> rilevazioni_glicemiche = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rilevazioni_glicemiche.add(new Rilevazione(
                        rs.getString("cf_paziente"),
                        rs.getInt("valore"),
                        rs.getTimestamp("data_ora").toLocalDateTime(),
                        rs.getString("tipo"),
                        rs.getString("tipo_pasto")
                ));
            }
        }

        return rilevazioni_glicemiche;
    }
}
