package com.musicplayer.service;

import com.musicplayer.dao.PlaylistDAO;
import com.musicplayer.dao.SongDAO;
import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MusicPlayerService {
    private static final Logger logger = LoggerFactory.getLogger(MusicPlayerService.class);
    
    private final SongDAO songDAO;
    private final PlaylistDAO playlistDAO;
    
    private MediaPlayer mediaPlayer;
    private ObservableList<Song> currentQueue;
    private int currentSongIndex = -1;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    public MusicPlayerService() {
        this.songDAO = new SongDAO();
        this.playlistDAO = new PlaylistDAO();
        this.currentQueue = FXCollections.observableArrayList();
    }

    // --- Media Player Controls ---

    public void playSong(Song song, ObservableList<Song> queue) {
        if (song == null) return;
        
        this.currentQueue = queue;
        this.currentSongIndex = currentQueue.indexOf(song);
        
        playCurrentSong();
    }
    
    private void playCurrentSong() {
        if (currentSongIndex < 0 || currentSongIndex >= currentQueue.size()) return;
        
        Song song = currentQueue.get(currentSongIndex);
        try {
            File file = new File(song.getFilePath());
            if (!file.exists()) {
                logger.error("File not found: {}", song.getFilePath());
                return;
            }
            
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            logger.info("Playing: {}", song.getTitle());
            
        } catch (Exception e) {
            logger.error("Error playing song", e);
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
    
    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void next() {
        if (currentQueue.isEmpty()) return;
        
        if (isShuffle) {
            currentSongIndex = new Random().nextInt(currentQueue.size());
        } else {
            currentSongIndex++;
            if (currentSongIndex >= currentQueue.size()) {
                currentSongIndex = isRepeat ? 0 : currentQueue.size() - 1;
            }
        }
        playCurrentSong();
    }

    public void previous() {
        if (currentQueue.isEmpty()) return;
        
        currentSongIndex--;
        if (currentSongIndex < 0) {
            currentSongIndex = isRepeat ? currentQueue.size() - 1 : 0;
        }
        playCurrentSong();
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public void setShuffle(boolean shuffle) {
        this.isShuffle = shuffle;
    }

    public void setRepeat(boolean repeat) {
        this.isRepeat = repeat;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public Song getCurrentSong() {
        if (currentSongIndex >= 0 && currentSongIndex < currentQueue.size()) {
            return currentQueue.get(currentSongIndex);
        }
        return null;
    }

    // --- Database Operations ---

    public void addSong(Song song) {
        songDAO.addSong(song);
    }

    public List<Song> getAllSongs() {
        return songDAO.getAllSongs();
    }
    
    public List<Song> searchSongs(String keyword) {
        return songDAO.searchSongs(keyword);
    }
    
    public void deleteSong(int id) {
        songDAO.deleteSong(id);
    }

    public void createPlaylist(Playlist playlist) {
        playlistDAO.createPlaylist(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

    public void deletePlaylist(int id) {
        playlistDAO.deletePlaylist(id);
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        playlistDAO.addSongToPlaylist(playlistId, songId);
    }

    public void removeSongFromPlaylist(int playlistId, int songId) {
        playlistDAO.removeSongFromPlaylist(playlistId, songId);
    }

    public List<Song> getSongsInPlaylist(int playlistId) {
        return playlistDAO.getSongsInPlaylist(playlistId);
    }
}
