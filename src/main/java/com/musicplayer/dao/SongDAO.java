package com.musicplayer.dao;

import com.musicplayer.model.Song;
import com.musicplayer.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {
    private static final Logger logger = LoggerFactory.getLogger(SongDAO.class);

    public void addSong(Song song) {
        String query = "INSERT INTO songs (title, artist, album, genre, duration, file_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, song.getTitle());
            stmt.setString(2, song.getArtist());
            stmt.setString(3, song.getAlbum());
            stmt.setString(4, song.getGenre());
            stmt.setDouble(5, song.getDuration());
            stmt.setString(6, song.getFilePath());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    song.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("Added song to database: {}", song.getTitle());
        } catch (SQLException e) {
            logger.error("Error adding song", e);
        }
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                songs.add(extractSongFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all songs", e);
        }
        return songs;
    }

    public List<Song> searchSongs(String keyword) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT * FROM songs WHERE title LIKE ? OR artist LIKE ? OR album LIKE ? OR genre LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(extractSongFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching songs with keyword: {}", keyword, e);
        }
        return songs;
    }
    
    public void deleteSong(int id) {
        String query = "DELETE FROM songs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Deleted song with id: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting song", e);
        }
    }

    private Song extractSongFromResultSet(ResultSet rs) throws SQLException {
        return new Song(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("artist"),
            rs.getString("album"),
            rs.getString("genre"),
            rs.getDouble("duration"),
            rs.getString("file_path")
        );
    }
}
