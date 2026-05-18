package com.musicplayer.model;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private double duration;
    private String filePath;

    public Song(int id, String title, String artist, String album, String genre, double duration, String filePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
    }

    public Song(String title, String artist, String album, String genre, double duration, String filePath) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
        this.filePath = filePath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
