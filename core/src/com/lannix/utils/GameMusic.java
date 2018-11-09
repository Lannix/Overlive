package com.lannix.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.JsonReader;

import static com.badlogic.gdx.math.MathUtils.random;

public class GameMusic {

    private String[] musicPaths;
    private Music music;

    public GameMusic(String musicPathsJson) {
        musicPaths = new JsonReader().parse(Gdx.files.internal(musicPathsJson)).asStringArray();
        music = Gdx.audio.newMusic(Gdx.files.internal(getRandomMusicPath()));
    }

    public void act(boolean playMusic) {
        if (playMusic) {
            if (!music.isPlaying()) {
                music.dispose();
                music = Gdx.audio.newMusic(Gdx.files.internal(getRandomMusicPath()));
                music.play();
            }
        } else if (music.isPlaying()) music.stop();
    }

    private String getRandomMusicPath() {
        return musicPaths[random(musicPaths.length - 1)];
    }

    public void dispose() {
        music.dispose();
    }
}
