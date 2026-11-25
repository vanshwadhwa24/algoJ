package com.algoj.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunHistoryDao implements RunHistoryDao {
    private final Database db;

    public JdbcRunHistoryDao(Database db) {
        this.db = db;
    }

    @Override
    public void record(String algorithmId, String input, int steps) {
        String sql = "INSERT INTO run_history (algorithm_id, input, steps, created_at) VALUES (?, ?, ?, ?)";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, algorithmId);
            ps.setString(2, input);
            ps.setInt(3, steps);
            ps.setLong(4, Instant.now().toEpochMilli());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Cannot record run", e);
        }
    }

    @Override
    public List<RunRecord> recent(int limit) {
        String sql = "SELECT id, algorithm_id, input, steps, created_at FROM run_history ORDER BY id DESC LIMIT ?";
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            List<RunRecord> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new RunRecord(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4), Instant.ofEpochMilli(rs.getLong(5))));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("Cannot read run history", e);
        }
    }

    @Override
    public int count() {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM run_history");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new DataAccessException("Cannot count runs", e);
        }
    }
}
