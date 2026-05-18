package com.musicplayer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    // SQLite will automatically create this file in the project folder!
    private static final String URL = "jdbc:sqlite:music_player.db";
    private static boolean initialized = false;

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        if (!initialized) {
            initializeDatabase(conn);
            initialized = true;
        }
        return conn;
    }

    private static void initializeDatabase(Connection conn) {
        try (java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS songs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "artist TEXT," +
                    "album TEXT," +
                    "genre TEXT," +
                    "duration REAL," +
                    "file_path TEXT NOT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS playlists (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS playlist_songs (" +
                    "playlist_id INTEGER," +
                    "song_id INTEGER," +
                    "PRIMARY KEY (playlist_id, song_id)," +
                    "FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE)");
            
            logger.info("SQLite Database initialized successfully!");
        } catch (SQLException e) {
            logger.error("Failed to initialize SQLite database", e);
        }
    }
}
