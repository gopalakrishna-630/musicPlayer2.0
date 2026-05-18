CREATE DATABASE IF NOT EXISTS music_player_db;
USE music_player_db;

CREATE TABLE IF NOT EXISTS songs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255),
    album VARCHAR(255),
    genre VARCHAR(100),
    duration DOUBLE,
    file_path TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS playlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS playlist_songs (
    playlist_id INT,
    song_id INT,
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE,
    PRIMARY KEY (playlist_id, song_id)
);

-- Insert sample data
INSERT INTO playlists (name) VALUES ('My Favorites');
