package diabetici.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SintomoDAO {
    private final Connection connection;

    public SintomoDAO(Connection connection) {
        this.connection = connection;
    }

    public void inserisciSintomo(String cfPaziente, String sintomo) throws SQLException {
        String query = "INSERT INTO sintomi_segnalati (cf_paziente, sintomo, data_ora) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            stmt.setString(2, sintomo);
            stmt.executeUpdate();
        }
    }


    public List<String> getSintomiSegnalatiUltime24h(String cfPaziente) throws SQLException {
        String query = "SELECT sintomo FROM sintomi_segnalati WHERE cf_paziente = ? AND data_ora >= NOW() - INTERVAL 1 DAY";
        List<String> sintomi = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfPaziente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sintomi.add(rs.getString("sintomo"));
            }
        }
        return sintomi;
    }
}
