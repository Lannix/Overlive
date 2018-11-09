package com.lannix.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.GameConstants.ONE_STAR;
import static com.lannix.game.utils.GameConstants.THREE_STARS;
import static com.lannix.game.utils.GameConstants.TWO_STARS;
import static com.lannix.utils.Settings.PLAYER_NAME;

public class LevelBuilder {

    private Json json;
    private GameData.Chapter.Level levelData;

    public LevelBuilder(GameData.Chapter.Level levelData) {
        this.levelData = levelData;
        json = new Json();
    }

    public void createLevel() {
    }

    public LevelText createLevelText(final I18NBundle levelBundle, final String levelType, final GameWorld world) {
        JsonValue value = new JsonReader().parse(Gdx.files.internal(levelData.dataPath));
        json.addClassTag("game_over", GameOver.class);
        GameOver gameOver = json.readValue("game_over", GameOver.class, value);

        final LevelText levelText;
        if (levelType.equals("tutorial")) {
            levelText = new TutorialText(levelBundle);
        } else if(levelType.equals("more_mass")) {
            levelText = new MoreMassText(world, levelBundle, gameOver.gameOverValue);
        } else if(levelType.equals("survival")) {
            levelText = new SurvivalText(world, levelBundle, gameOver.gameOverValue);
        } else if(levelType.equals("labyrinth")) {
            levelText = new LabyrinthText(world, levelBundle, gameOver.gameOverValue);
        } else if(levelType.equals("mission")) {
            levelText = new MissionText(world, levelBundle, gameOver);
        } else {
            //default
            levelText = new TutorialText(levelBundle);
        }
        return levelText;
    }

    public Dialog createDialog(I18NBundle levelBundle) {
        json.addClassTag("dialog", Dialog.class);
        JsonValue value = new JsonReader().parse(Gdx.files.internal(levelData.dataPath)).getChild("levelData");
        Dialog dialog = json.readValue(Dialog.class, value);
        if (dialog != null) {
            for (int i = 0; i < dialog.dialogs.length; i++) {
                dialog.dialogs[i] = levelBundle.format(dialog.dialogs[i], PLAYER_NAME);
            }
        }
        return dialog;
    }


    /********************Static-inner Classes******************/

    public interface LevelText {
        String createText();
    }

    public static class TutorialText implements LevelText {
        private I18NBundle levelBundle;

        public TutorialText(I18NBundle levelBundle) {
            this.levelBundle = levelBundle;
        }

        @Override
        public String createText() {
            return levelBundle.get("tutorial_level_text");
        }
    }

    public static class MoreMassText implements LevelText {
        private GameWorld gameWorld;
        private I18NBundle levelBundle;
        private int maxMass;

        public MoreMassText(GameWorld gameWorld, I18NBundle levelBundle, int maxMass) {
            this.gameWorld = gameWorld;
            this.levelBundle = levelBundle;
            this.maxMass = maxMass;
        }

        @Override
        public String createText() {
            return levelBundle.format("more_mass_level_text", (int) gameWorld.getPlayerMass(),
                    (int) (maxMass * ONE_STAR), (int) (maxMass * TWO_STARS), (int) (maxMass * THREE_STARS));
        }
    }

    public static class SurvivalText implements LevelText {
        private GameWorld gameWorld;
        private I18NBundle levelBundle;
        private int time;

        public SurvivalText(GameWorld gameWorld, I18NBundle levelBundle, int time) {
            this.gameWorld = gameWorld;
            this.levelBundle = levelBundle;
            this.time = time;
        }

        @Override
        public String createText() {
            return levelBundle.format("survival_level_text", (int) gameWorld.getCurrentGameTimeSec(),
                    (int) (time * ONE_STAR), (int) (time * TWO_STARS), (int) (time * THREE_STARS));
        }
    }

    public static class LabyrinthText implements LevelText {
        private GameWorld gameWorld;
        private I18NBundle levelBundle;
        private int time;

        public LabyrinthText(GameWorld gameWorld, I18NBundle levelBundle, int time) {
            this.gameWorld = gameWorld;
            this.levelBundle = levelBundle;
            this.time = time;
        }

        @Override
        public String createText() {
            return levelBundle.format("labyrinth_level_text", (int) gameWorld.getPlayerMass(),
                    (int) (time * ONE_STAR), (int) (time * TWO_STARS), (int) (time * THREE_STARS));
        }
    }

    public static class MissionText implements LevelText {
        private GameWorld gameWorld;
        private I18NBundle levelBundle;
        private GameOver gameOver;

        public MissionText(GameWorld gameWorld, I18NBundle levelBundle, GameOver gameOver) {
            this.gameWorld = gameWorld;
            this.levelBundle = levelBundle;
            this.gameOver = gameOver;
        }

        @Override
        public String createText() {
            return levelBundle.format("labyrinth_level_text", gameOver.missionText,
                    (int) gameWorld.getTarget().sub(gameWorld.getPlayerPosition()).len());
        }
    }

    public static class GameOver {
        public int gameOverValue = 0;
        public String missionText;
    }

    public static class Dialog {
        public String[] dialogs;
        public String[] imagesFromGameAtlas;
        public int[] imageIndexes;
    }
}
