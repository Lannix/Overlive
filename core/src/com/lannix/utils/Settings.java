package com.lannix.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.lannix.game.utils.GameData;

import static com.lannix.utils.Constants.RU;

public class Settings {

    public static volatile String LANGUAGE = RU;
    public static volatile boolean PLAY_MUSIC = true;
    public static volatile String PLAYER_NAME = "";
    public static volatile GameData.Chapter.Level LAST_LEVEL_DATA;

    public static void save() {
        Preferences preferences = Gdx.app.getPreferences("Settings");
        preferences.putString("LANGUAGE", LANGUAGE);
        preferences.putBoolean("PLAY_MUSIC", PLAY_MUSIC);
        preferences.putString("PLAYER_NAME", PLAYER_NAME);
        preferences.putString("LAST_LEVEL_DATA", new Json().toJson(LAST_LEVEL_DATA, GameData.Chapter.Level.class));
        preferences.flush();
    }

    public static void load() {
        Preferences preferences = Gdx.app.getPreferences("Settings");
        LANGUAGE = preferences.getString("LANGUAGE", LANGUAGE);
        PLAY_MUSIC = preferences.getBoolean("PLAY_MUSIC", PLAY_MUSIC);
        PLAYER_NAME = preferences.getString("PLAYER_NAME", PLAYER_NAME);
        LAST_LEVEL_DATA = new Json().fromJson(GameData.Chapter.Level.class, preferences.getString("LAST_LEVEL_DATA"));
    }

    public static void saveNewLastLevel(GameData.Chapter.Level levelData) {
        Preferences preferences = Gdx.app.getPreferences("Settings");
        preferences.putString("LAST_LEVEL_DATA", new Json().toJson(levelData, GameData.Chapter.Level.class));
        preferences.flush();
    }

    public static void loadNewLastLevel() {
        Preferences preferences = Gdx.app.getPreferences("Settings");
        LAST_LEVEL_DATA = new Json().fromJson(GameData.Chapter.Level.class, preferences.getString("LAST_LEVEL_DATA"));
    }

    public static void loadDefault() {
        LANGUAGE = "en";
        PLAY_MUSIC = true;
    }
}
