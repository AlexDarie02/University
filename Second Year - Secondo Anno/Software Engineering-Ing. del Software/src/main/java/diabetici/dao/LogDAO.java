package diabetici.dao;

import mcv.model.LogAzione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogDAO {
    private final Connection conn;

    public LogDAO(Connection conn) {
        this.conn = conn;
    }

    public void inserisciLog(LogAzione log) throws SQLException {
        String sql = "INSERT INTO log_azioni (codice_fiscale_medico, codice_fiscale_paziente, azione, data_ora) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getMedicoCF());
            ps.setString(2, log.getPazienteCF());
            ps.setString(3, log.getAzione());
            ps.setTimestamp(4, Timestamp.valueOf(log.getDataOra()));
            ps.executeUpdate();
        }
    }
}
