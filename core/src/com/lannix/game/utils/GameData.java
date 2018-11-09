package com.lannix.game.utils;

import com.badlogic.gdx.graphics.Color;

public class GameData {
    public Chapter[] chapters;

    public static class Chapter {
        public String name;
        public int chapterNumber;
        public Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 1f);
        public Color foregroundColor = Color.DARK_GRAY;
        public Color textColor = Color.WHITE;
        public int maxPoints;
        public Level[] levels;

        public static class Level {
            public int chapterNumber;
            public int levelNumber;
            public String name;
            public String dataPath;
            public String localePath;
            public String type;
            public Color backgroundColor = Color.DARK_GRAY;
            public Color foregroundColor = Color.GRAY;
            public Color textColor = Color.WHITE;
            public Color pointsColor = Color.YELLOW;
            public int maxPoints = 3;
        }
    }
}
