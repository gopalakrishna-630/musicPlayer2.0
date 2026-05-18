package com.musicplayer.dao;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;
import com.musicplayer.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {
    private static final Logger logger = LoggerFactory.getLogger(PlaylistDAO.class);

    public void createPlaylist(Playlist playlist) {
        String query = "INSERT INTO playlists (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, playlist.getName());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    playlist.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("Created playlist: {}", playlist.getName());
        } catch (SQLException e) {
            logger.error("Error creating playlist", e);
        }
    }

    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT * FROM playlists";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Playlist playlist = new Playlist(rs.getInt("id"), rs.getString("name"));
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving playlists", e);
        }
        return playlists;
    }
    
    public void deletePlaylist(int id) {
        String query = "DELETE FROM playlists WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Deleted playlist with id: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting playlist", e);
        }
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        String query = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
            logger.info("Added song {} to playlist {}", songId, playlistId);
        } catch (SQLException e) {
            logger.error("Error adding song to playlist", e);
        }
    }

    public void removeSongFromPlaylist(int playlistId, int songId) {
        String query = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            stmt.executeUpdate();
            logger.info("Removed song {} from playlist {}", songId, playlistId);
        } catch (SQLException e) {
            logger.error("Error removing song from playlist", e);
        }
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT s.* FROM songs s JOIN playlist_songs ps ON s.id = ps.song_id WHERE ps.playlist_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, playlistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songs.add(new Song(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("genre"),
                        rs.getDouble("duration"),
                        rs.getString("file_path")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving songs for playlist: {}", playlistId, e);
        }
        return songs;
    }
}
