package com.musicplayer.controller;

import com.musicplayer.model.Playlist;
import com.musicplayer.model.Song;
import com.musicplayer.service.MusicPlayerService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private TableView<Song> songTable;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;
    
    @FXML private ListView<Playlist> playlistView;
    @FXML private TextField searchField;
    @FXML private Label currentSongLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    
    @FXML private Button playPauseBtn;
    
    private MusicPlayerService service;
    private ObservableList<Song> currentSongList;
    private ObservableList<Playlist> playlistObservableList;
    
    private boolean isPlaying = false;
    private Playlist currentPlaylist = null; // null means "All Songs"
    
    @FXML private HBox titleBar;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        service = new MusicPlayerService();
        currentSongList = FXCollections.observableArrayList();
        playlistObservableList = FXCollections.observableArrayList();

        setupTableColumns();
        setupListeners();
        
        loadAllSongs();
        loadPlaylists();
        
        volumeSlider.setValue(50);
        
        if (titleBar != null) {
            titleBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) titleBar.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        durationColumn.setCellValueFactory(cellData -> {
            double duration = cellData.getValue().getDuration();
            return new SimpleStringProperty(formatDuration(Duration.seconds(duration)));
        });
        
        songTable.setItems(currentSongList);
    }

    private void setupListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                if (currentPlaylist == null) loadAllSongs();
                else loadPlaylistSongs(currentPlaylist);
            } else {
                List<Song> results = service.searchSongs(newValue);
                currentSongList.setAll(results);
            }
        });

        songTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Song selected = songTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    playSelectedSong(selected);
                }
            }
        });

        playlistView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentPlaylist = newVal;
                loadPlaylistSongs(newVal);
            }
        });

        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (progressSlider.isValueChanging() && service.getMediaPlayer() != null) {
                service.getMediaPlayer().seek(Duration.seconds(newValue.doubleValue()));
            }
        });

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            service.setVolume(newValue.doubleValue() / 100.0);
        });
    }

    private void playSelectedSong(Song song) {
        service.playSong(song, currentSongList);
        isPlaying = true;
        updatePlayPauseButton();
        updateUIForCurrentSong();
    }

    private void updateUIForCurrentSong() {
        Song current = service.getCurrentSong();
        if (current != null) {
            currentSongLabel.setText(current.getTitle() + " - " + current.getArtist());
            
            // Keep the table cursor highlighting the currently playing song
            songTable.getSelectionModel().select(current);
            songTable.scrollTo(current);
            
            MediaPlayer player = service.getMediaPlayer();
            
            if (player == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to play audio. Your Linux system may be missing required audio codecs (like gstreamer1.0-libav or libavformat-dev).");
                alert.show();
                return;
            }
            
            player.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newValue.toSeconds());
                }
                currentTimeLabel.setText(formatDuration(newValue));
            });

            player.setOnReady(() -> {
                Duration total = player.getMedia().getDuration();
                progressSlider.setMax(total.toSeconds());
                totalTimeLabel.setText(formatDuration(total));
            });

            player.setOnEndOfMedia(() -> {
                onNext();
            });
        }
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) duration.toSeconds() % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updatePlayPauseButton() {
        playPauseBtn.setText(isPlaying ? "⏸" : "▶");
    }

    @FXML
    public void onPlayPause() {
        if (service.getMediaPlayer() == null) {
            Song selected = songTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                playSelectedSong(selected);
            } else if (!currentSongList.isEmpty()) {
                playSelectedSong(currentSongList.get(0));
            }
            return;
        }

        if (isPlaying) {
            service.pause();
        } else {
            service.resume();
        }
        isPlaying = !isPlaying;
        updatePlayPauseButton();
    }

    @FXML
    public void onNext() {
        service.next();
        isPlaying = true;
        updatePlayPauseButton();
        updateUIForCurrentSong();
    }

    @FXML
    public void onPrevious() {
        service.previous();
        isPlaying = true;
        updatePlayPauseButton();
        updateUIForCurrentSong();
    }

    @FXML
    public void onImport() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Music Folder");
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        
        if (selectedDirectory != null) {
            scanDirectory(selectedDirectory);
        }
    }

    private void scanDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file);
                } else {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                        String title = file.getName();
                        String artist = "Unknown Artist";
                        String album = "Unknown Album";
                        String genre = "Unknown";
                        double duration = 0.0;
                        
                        try {
                            if (name.endsWith(".mp3")) {
                                com.mpatric.mp3agic.Mp3File mp3file = new com.mpatric.mp3agic.Mp3File(file.getAbsolutePath());
                                duration = mp3file.getLengthInSeconds();
                                if (mp3file.hasId3v2Tag()) {
                                    com.mpatric.mp3agic.ID3v2 tag = mp3file.getId3v2Tag();
                                    if (tag.getTitle() != null && !tag.getTitle().trim().isEmpty()) title = tag.getTitle();
                                    if (tag.getArtist() != null && !tag.getArtist().trim().isEmpty()) artist = tag.getArtist();
                                    if (tag.getAlbum() != null && !tag.getAlbum().trim().isEmpty()) album = tag.getAlbum();
                                    if (tag.getGenreDescription() != null) genre = tag.getGenreDescription();
                                } else if (mp3file.hasId3v1Tag()) {
                                    com.mpatric.mp3agic.ID3v1 tag = mp3file.getId3v1Tag();
                                    if (tag.getTitle() != null && !tag.getTitle().trim().isEmpty()) title = tag.getTitle();
                                    if (tag.getArtist() != null && !tag.getArtist().trim().isEmpty()) artist = tag.getArtist();
                                    if (tag.getAlbum() != null && !tag.getAlbum().trim().isEmpty()) album = tag.getAlbum();
                                    if (tag.getGenreDescription() != null) genre = tag.getGenreDescription();
                                }
                            }
                        } catch (Exception e) {
                            // Silently fall back to defaults if parsing fails
                        }

                        Song song = new Song(title, artist, album, genre, duration, file.getAbsolutePath());
                        service.addSong(song); // Tries to save to DB
                        currentSongList.add(song); // Instantly show in UI even if DB fails
                    }
                }
            }
        }
    }

    @FXML
    public void onShowAllSongs() {
        currentPlaylist = null;
        playlistView.getSelectionModel().clearSelection();
        loadAllSongs();
    }

    @FXML
    public void onCreatePlaylist() {
        TextInputDialog dialog = new TextInputDialog("New Playlist");
        dialog.setTitle("Create Playlist");
        dialog.setHeaderText("Enter playlist name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Playlist playlist = new Playlist(name);
            service.createPlaylist(playlist);
            loadPlaylists();
        });
    }

    @FXML
    public void onAddToPlaylist() {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        Playlist selectedPlaylist = playlistView.getSelectionModel().getSelectedItem();
        
        if (selectedSong != null && selectedPlaylist != null) {
            service.addSongToPlaylist(selectedPlaylist.getId(), selectedSong.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Song added to " + selectedPlaylist.getName());
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select both a song and a playlist");
            alert.show();
        }
    }

    private void loadAllSongs() {
        currentSongList.setAll(service.getAllSongs());
    }

    private void loadPlaylists() {
        playlistObservableList.setAll(service.getAllPlaylists());
        playlistView.setItems(playlistObservableList);
    }

    private void loadPlaylistSongs(Playlist playlist) {
        currentSongList.setAll(service.getSongsInPlaylist(playlist.getId()));
    }
    
    @FXML
    public void onMinimize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void onMaximize() {
        Stage stage = (Stage) titleBar.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    public void onClose() {
        Platform.exit();
    }
}
