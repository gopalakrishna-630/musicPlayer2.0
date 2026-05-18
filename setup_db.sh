#!/bin/bash
echo "Setting up MySQL Database for Music Player..."
sudo mysql -e "CREATE DATABASE IF NOT EXISTS music_player_db;"
sudo mysql -e "CREATE USER IF NOT EXISTS 'music_admin'@'localhost' IDENTIFIED BY 'music123';"
sudo mysql -e "ALTER USER 'music_admin'@'localhost' IDENTIFIED BY 'music123';"
sudo mysql -e "GRANT ALL PRIVILEGES ON music_player_db.* TO 'music_admin'@'localhost';"
sudo mysql -e "FLUSH PRIVILEGES;"

echo "Importing database tables..."
mysql -u music_admin -pmusic123 music_player_db < database/music_player.sql

echo "Database setup complete! You can now run the app and your songs will be saved permanently."
