package com.algoj.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class JdbcQuizAttemptDao implements QuizAttemptDao {
    private final Database db;

    public JdbcQuizAttemptDao(Database db) {
        this.db = db;
    }

    @Override
    public boolean record(String questionId, String algorithmId, boolean correct) {
        try (Connection c = db.connect()) {
            c.setAutoCommit(false);
            try {
                boolean earnsPoints = correct && !hasCorrect(c, questionId);
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO quiz_attempt (question_id, algorithm_id, correct, created_at) VALUES (?, ?, ?, ?)")) {
                    ps.setString(1, questionId);
                    ps.setString(2, algorithmId);
                    ps.setInt(3, correct ? 1 : 0);
                    ps.setLong(4, Instant.now().toEpochMilli());
                    ps.executeUpdate();
                }
                c.commit();
                return earnsPoints;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cannot record quiz attempt", e);
        }
    }

    private static boolean hasCorrect(Connection c, String questionId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM quiz_attempt WHERE question_id = ? AND correct = 1 LIMIT 1")) {
            ps.setString(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean isAnswered(String questionId) {
        return scalar("SELECT COUNT(*) FROM quiz_attempt WHERE question_id = ?", questionId) > 0;
    }

    @Override
    public int correctQuestionCount() {
        return scalar("SELECT COUNT(DISTINCT question_id) FROM quiz_attempt WHERE correct = 1", null);
    }

    @Override
    public int attemptCount() {
        return scalar("SELECT COUNT(*) FROM quiz_attempt", null);
    }

    private int scalar(String sql, String arg) {
        try (Connection c = db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (arg != null) ps.setString(1, arg);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cannot read quiz data", e);
        }
    }
}
