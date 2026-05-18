package com.musicplayer;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class TestAudio {
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                System.out.println("Starting test...");
                File file = new File("/home/gopalakrishna/Downloads/audio/05_Naatu_Naatu.mp3");
                if (!file.exists()) {
                    System.out.println("File does not exist");
                    Platform.exit();
                    return;
                }
                System.out.println("URI: " + file.toURI().toString());
                Media m = new Media(file.toURI().toString());
                System.out.println("Media created successfully.");
                MediaPlayer mp = new MediaPlayer(m);
                System.out.println("MediaPlayer created successfully.");
                
                mp.setOnError(() -> {
                    System.out.println("MediaPlayer ERROR: " + mp.getError());
                    Platform.exit();
                });
                
                mp.setOnReady(() -> {
                    System.out.println("MediaPlayer READY.");
                    Platform.exit();
                });
                
            } catch (Exception e) {
                System.out.println("EXCEPTION CAUGHT:");
                e.printStackTrace();
                Platform.exit();
            }
        });
    }
}
