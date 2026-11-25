package com.algoj.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Opens SQLite connections through plain JDBC and makes sure the schema exists. */
public final class Database {

    private final String url;

    private Database(String url) {
        this.url = url;
    }

    /** A database stored in a file, created with its schema on first use. */
    public static Database open(Path file) {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException e) {
            throw new DataAccessException("Cannot create database folder for " + file, e);
        }
        Database db = new Database("jdbc:sqlite:" + file.toAbsolutePath());
        db.createSchema();
        return db;
    }

    /** The default per-user location, e.g. ~/.algoj/algoj.db. */
    public static Path defaultFile() {
        return Path.of(System.getProperty("user.home"), ".algoj", "algoj.db");
    }

    public Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    private void createSchema() {
        try (InputStream in = Database.class.getResourceAsStream("/schema.sql")) {
            if (in == null) throw new DataAccessException("schema.sql missing from resources", null);
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection c = connect(); Statement st = c.createStatement()) {
                for (String sql : script.split(";")) {
                    if (!sql.isBlank()) st.execute(sql);
                }
            }
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Cannot create schema", e);
        }
    }
}
