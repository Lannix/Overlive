package com.lannix.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class ProgressData {

    public static final String PROGRESS_FILE_NAME = "progress_data.json";
    public boolean cheatOpen = false;
    public Chapter[] chapters;

    public ProgressData() {
    }

    public ProgressData(GameData data) {
        chapters = new Chapter[data.chapters.length];
        for (int i = 0; i < data.chapters.length; i++) {
            chapters[i] = new Chapter();
            chapters[i].levels = new Chapter.Level[data.chapters[i].levels.length];
            for (int j = 0; j < data.chapters[i].levels.length; j++) {
                chapters[i].levels[j] = new Chapter.Level();
            }
        }
    }

    public static class Chapter {
        public int scoredPoints = 0;
        public boolean cheatOpen = false;
        public Level[] levels;

        public static class Level {
            public int scoredPoints = 0;
            public boolean cheatOpen = false;
        }
    }

    public static void saveNewScore(final int chapter, final int level, final int newScore) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                ProgressData progressData = json.fromJson(ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
                int oldScore = progressData.chapters[chapter].levels[level].scoredPoints;
                if (oldScore < newScore) {
                    progressData.chapters[chapter].scoredPoints += newScore - oldScore;
                    progressData.chapters[chapter].levels[level].scoredPoints = newScore;
                    json.toJson(progressData, ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
                }
            }
        }).start();
    }

    private static void openAllChaptersByCheat() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                ProgressData progressData = json.fromJson(ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
                progressData.cheatOpen = true;
                json.toJson(progressData, ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
            }
        }).start();
    }

    public static void openChapterByCheat(final int chapter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                ProgressData progressData = json.fromJson(ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
                progressData.chapters[chapter].cheatOpen = true;
                json.toJson(progressData, ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
            }
        }).start();
    }

    public static void openLevelByCheat(final int chapter, final int level) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                ProgressData progressData = json.fromJson(ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
                progressData.chapters[chapter].levels[level].cheatOpen = true;
                json.toJson(progressData, ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
            }
        }).start();
    }
}
