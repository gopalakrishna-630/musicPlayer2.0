# Music Player with Playlist Management 🎵

A production-ready, full-featured desktop music player application built with Java and JavaFX. It features a modern, dark-themed UI inspired by popular streaming platforms like Spotify, offering a premium and immersive audio experience.

## 🚀 Features

* **Modern Dark UI**: A sleek, borderless user interface with custom minimize/maximize/close window controls.
* **Audio Playback**: Full support for `.mp3` and `.wav` audio files using the JavaFX Media Engine.
* **Metadata Extraction**: Automatically reads ID3 tags (Title, Artist, Album, Duration) from imported `.mp3` files using `mp3agic`.
* **Smart Library Management**: Recursively scans entire folders and adds all compatible music files to your central library.
* **Database Integration**: Persistent storage of songs and custom playlists using an embedded, zero-configuration SQLite database.
* **One-Click Playback**: Instantly play any song with a single click in your library or playlist view.
* **Playback Controls**: Play, Pause, Next, Previous, Volume Slider, and a dynamic Progress Seeker.

## 🛠️ Technology Stack

* **Language**: Java 17
* **GUI Framework**: JavaFX 22.0.1 (Undecorated Stage & CSS Styling)
* **Database**: SQLite (via JDBC Driver)
* **Audio Parsing**: `mp3agic` (ID3v1 & ID3v2 metadata parsing)
* **Build Tool**: Maven

## 📋 Prerequisites

Before running the application, ensure you have the following installed on your system:
* **JDK 17** (or newer)
* **Maven**
* *(Linux Only)* Required audio codecs for JavaFX: `sudo apt-get install libavformat-dev gstreamer1.0-libav gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly`

## ⚙️ Database Setup

**Zero Setup Required!** 
This application uses a fully embedded **SQLite** database. When you run the application for the first time, it will automatically generate a `music_player.db` file in the project folder and configure all the necessary tables for you!

## 🏃‍♂️ Running the Application

To compile and launch the application, navigate to the project root directory and run:

```bash
mvn clean javafx:run
```

## 🎮 How to Use

1. **Importing Music**: Click "Import Music" on the left sidebar, select a folder containing `.mp3` or `.wav` files, and the app will instantly scan and extract the metadata for all songs.
2. **Playing Music**: Click on any song in the list once to start playback.
3. **Playlists**: Click the `+` icon next to "Playlists" to create a new playlist. Select a song and click "Add to Playlist" to organize your library.
