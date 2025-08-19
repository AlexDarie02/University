package diabetici.dao;

import mcv.model.SegnalazioneGlicemia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SegnalazioneGlicemiaDAO {

    private final Connection connection;

    public SegnalazioneGlicemiaDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean inserisciSegnalazione(SegnalazioneGlicemia segnalazione, String cfMedico) throws SQLException {
        String query = "INSERT INTO segnalazioni (cf_paziente, tipo, valore, data_ora, cf_medico, categoria) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, segnalazione.getCfPaziente());
            stmt.setString(2, segnalazione.getTipoMisurazione());
            stmt.setDouble(3, segnalazione.getValore());
            stmt.setTimestamp(4, Timestamp.valueOf(segnalazione.getDataOra()));
            stmt.setString(5, cfMedico);
            stmt.setString(6, segnalazione.getCategoria());

            return stmt.executeUpdate() > 0;
        }
    }

    public List<SegnalazioneGlicemia> getSegnalazioniPerMedico(String cfMedico) throws SQLException {
        List<SegnalazioneGlicemia> lista = new ArrayList<>();

        String query = "SELECT cf_paziente, tipo, valore, data_ora, categoria FROM segnalazioni WHERE cf_medico = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cfMedico);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SegnalazioneGlicemia segnalazione = new SegnalazioneGlicemia(
                        rs.getString("cf_paziente"),
                        rs.getString("tipo"),
                        rs.getDouble("valore"),
                        rs.getTimestamp("data_ora").toLocalDateTime(),
                        rs.getString("categoria")
                );
                lista.add(segnalazione);
            }
        }

        return lista;
    }
}
