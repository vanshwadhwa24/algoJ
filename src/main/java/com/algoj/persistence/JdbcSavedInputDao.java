package com.algoj.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcSavedInputDao implements SavedInputDao {
    private final Database db;

    public JdbcSavedInputDao(Database db) {
        this.db = db;
    }

    /** Replace-by-name runs as one transaction so a failed insert never loses the old input. */
    @Override
    public void save(String algorithmId, String name, String payload) {
        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try (PreparedStatement del = c.prepareStatement("DELETE FROM saved_input WHERE algorithm_id = ? AND name = ?");
                 PreparedStatement ins = c.prepareStatement(
                         "INSERT INTO saved_input (algorithm_id, name, payload, created_at) VALUES (?, ?, ?, ?)")) {
                del.setString(1, algorithmId);
                del.setString(2, name);
                del.executeUpdate();
                ins.setString(1, algorithmId);
                ins.setString(2, name);
                ins.setString(3, payload);
                ins.setLong(4, Instant.now().toEpochMilli());
                ins.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cannot save input", e);
        }
    }

    @Override
    public List<SavedInput> forAlgorithm(String algorithmId) {
        String sql = "SELECT id, algorithm_id, name, payload, created_at FROM saved_input WHERE algorithm_id = ? ORDER BY id DESC";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, algorithmId);
            List<SavedInput> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new SavedInput(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), Instant.ofEpochMilli(rs.getLong(5))));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("Cannot read saved inputs", e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM saved_input WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Cannot delete input", e);
        }
    }
}
