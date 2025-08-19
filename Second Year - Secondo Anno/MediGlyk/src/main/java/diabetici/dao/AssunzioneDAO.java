package diabetici.dao;

import mcv.model.Assunzione;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssunzioneDAO {

    private final Connection conn;

    public AssunzioneDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertAssunzione(Assunzione a) {
        String sql = "INSERT INTO assunzioni (codice_fiscale, farmaco, dosaggio, data_ora) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getCodiceFiscale());
            ps.setString(2, a.getFarmaco());
            ps.setString(3, a.getDosaggio());
            ps.setTimestamp(4, Timestamp.valueOf(a.getDataOra()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Errore nell'inserimento assunzione", e);
        }
    }

    public List<Assunzione> getAssunzioniByPaziente(String codiceFiscale) {
        String sql = "SELECT * FROM assunzioni WHERE codice_fiscale = ? ORDER BY data_ora DESC";
        List<Assunzione> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Assunzione(
                        rs.getString("codice_fiscale"),
                        rs.getString("farmaco"),
                        rs.getString("dosaggio"),
                        rs.getTimestamp("data_ora").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero assunzioni", e);
        }
        return lista;
    }

    public LocalDateTime getUltimaAssunzione(String codiceFiscale) {
        String sql = "SELECT data_ora FROM assunzioni WHERE codice_fiscale = ? ORDER BY data_ora DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("data_ora").toLocalDateTime();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero ultima assunzione", e);
        }
        return null;
    }

    public List<Assunzione> getAssunzioniInsulinaByPeriodo(String codiceFiscale, LocalDateTime da, LocalDateTime a) {
        String sql = "SELECT * FROM assunzioni WHERE codice_fiscale = ? AND LOWER(farmaco) = 'insulina' AND data_ora BETWEEN ? AND ? ORDER BY data_ora ASC";
        List<Assunzione> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codiceFiscale);
            ps.setTimestamp(2, Timestamp.valueOf(da));
            ps.setTimestamp(3, Timestamp.valueOf(a));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Assunzione(
                        rs.getString("codice_fiscale"),
                        rs.getString("farmaco"),
                        rs.getString("dosaggio"),
                        rs.getTimestamp("data_ora").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore nel recupero assunzioni di insulina", e);
        }
        return lista;
    }



}
