package com.lannix.game.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lannix.game.utils.GameData;
import com.lannix.utils.GameMusic;
import com.lannix.utils.skin.MySkin;
import com.lannix.utils.skin.MySkinLoader;
import com.lannix.view.basic.BasicScreen;
import com.lannix.view.main_menu.Levels;

import java.util.Locale;

import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_HEIGHT;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_WIDTH;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.Settings.PLAY_MUSIC;
import static com.lannix.view.basic.ScreenLoader.swapScreenWithLoader;

public class GameOver extends BasicScreen {

    private Stage stage;
    private GameMusic gameMusic;
    private AssetManager manager;
    private GameData.Chapter.Level levelData;
    private Integer score, maxScore;

    public GameOver(Game game, GameData.Chapter.Level levelData, int score, int maxScore) {
        super(game);
        this.levelData = levelData;
        this.score = score;
        this.maxScore = maxScore;
    }

    @Override
    public void loadAssetManagerData(AssetManager manager) {
        this.manager = manager;
        manager.load("view/game/atoms.atlas", TextureAtlas.class);

        manager.setLoader(MySkin.class, new MySkinLoader(new InternalFileHandleResolver()));
        manager.load("view/ui/my_skin/skin_ui.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/my_skin/skin_ui.atlas"));
        manager.load("view/ui/levels/levels_skin.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/levels/levels_skin.atlas"));

        manager.setLoader(I18NBundle.class, new I18NBundleLoader(new InternalFileHandleResolver()));
        manager.load("gameplay/MyBundle", I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(new Locale(LANGUAGE), DEFAULT_I18N_ENCODING));

        manager.load("sounds/tap.mp3", Sound.class);
        manager.load("sounds/win.wav", Sound.class);
    }

    @Override
    public void show() {
        float w = VIRTUAL_SCREEN_WIDTH / 100f;
        float h = VIRTUAL_SCREEN_HEIGHT / 100f;

        //Initialize Resources
        gameMusic = new GameMusic("music/menu/menuMusicList.json");
        MySkin mySkin = manager.get("view/ui/my_skin/skin_ui.json", MySkin.class);
        MySkin levelsSkin = manager.get("view/ui/levels/levels_skin.json", MySkin.class);
        TextureAtlas gameAtlas = manager.get("view/game/atoms.atlas", TextureAtlas.class);
        I18NBundle myBundle = manager.get("gameplay/MyBundle", I18NBundle.class);
        final Sound tap = manager.get("sounds/tap.mp3", Sound.class);
        Sound win = manager.get("sounds/win.wav", Sound.class);
        win.play();

        //Initialize Stage
        stage = new Stage(new FitViewport(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);


        /********************UI*****************/
        //MainTable
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(50f);
        mainTable.center();
        mainTable.defaults().space(10f);
        stage.addActor(mainTable);

        //Image
        Image moleHoleImage = new Image(gameAtlas.findRegion("mole-hole"));
        moleHoleImage.setSize(Math.min(w * 40f, h * 40f), Math.min(w * 40f, h * 40f));
        mainTable.add(moleHoleImage).space(20f).size(moleHoleImage.getWidth(), moleHoleImage.getHeight()).row();

        //Points Horizontal Group
        HorizontalGroup pointsGroup = new HorizontalGroup();
        pointsGroup.space(10f);
        for (int i = 0; i < maxScore; i++) {
            Image pointImage = new Image();
            if (score > 0) {
                pointImage.setDrawable(levelsSkin, "point");
            } else {
                pointImage.setDrawable(levelsSkin, "closed_point");
            }
            pointsGroup.addActor(pointImage);
            score--;
        }
        mainTable.add(pointsGroup).row();

        //Game Over Label
        Label gameOverLabel = new Label(myBundle.get("game_over"),mySkin, "big");
        mainTable.add(gameOverLabel).row();

        //Next Level Button
        TextButton nextLevelButton = new TextButton(myBundle.get("next_level"), mySkin, "default");
        nextLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();

                Json json = new Json();
                json.addClassTag("chapter", GameData.Chapter.class);
                json.addClassTag("level", GameData.Chapter.Level.class);
                json.addClassTag("color", Color.class);

                GameData gameData = json.fromJson(GameData.class, Gdx.files.internal("gameplay/game_data.json"));
                int levelNumber = levelData.levelNumber + 1;
                if (levelNumber < gameData.chapters[levelData.chapterNumber].levels.length) {
                    GameData.Chapter.Level nextLevel = gameData.chapters[levelData.chapterNumber].levels[levelNumber];
                    swapScreenWithLoader(new GameLevel(game, nextLevel));
                }
            }
        });
        mainTable.add(nextLevelButton).row();

        //Levels Button
        TextButton levelsButton = new TextButton(myBundle.get("levels"), mySkin, "default");
        levelsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new Levels(game));
            }
        });
        mainTable.add(levelsButton).row();
    }

    @Override
    public void render(float delta) {
        gameMusic.act(PLAY_MUSIC);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        manager.dispose();
        gameMusic.dispose();
    }
}
