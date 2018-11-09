package com.lannix.view.main_menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lannix.game.utils.GameData;
import com.lannix.game.utils.ProgressData;
import com.lannix.game.view.GameLevel;
import com.lannix.utils.GameMusic;
import com.lannix.utils.skin.MySkin;
import com.lannix.utils.skin.MySkinLoader;
import com.lannix.view.basic.BasicScreen;

import java.util.Locale;

import static com.lannix.game.utils.ProgressData.PROGRESS_FILE_NAME;
import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_HEIGHT;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_WIDTH;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.Settings.PLAY_MUSIC;
import static com.lannix.view.basic.ScreenLoader.swapScreenWithLoader;

public class Levels extends BasicScreen {

    private Stage stage;
    private GameMusic gameMusic;
    private AssetManager manager;
    private GameData gameData;
    private ProgressData progressData;

    public Levels(Game game) {
        super(game);
    }

    @Override
    public void loadAssetManagerData(AssetManager manager) {
        this.manager = manager;

        manager.setLoader(MySkin.class, new MySkinLoader(new InternalFileHandleResolver()));
        manager.load("view/ui/default_skin/uiskin.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/default_skin/uiskin.atlas"));
        manager.load("view/ui/my_skin/skin_ui.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/my_skin/skin_ui.atlas"));
        manager.load("view/ui/levels/levels_skin.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/levels/levels_skin.atlas"));

        manager.setLoader(I18NBundle.class, new I18NBundleLoader(new InternalFileHandleResolver()));
        manager.load("gameplay/MyBundle", I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(new Locale(LANGUAGE), DEFAULT_I18N_ENCODING));

        manager.load("sounds/tap.mp3", Sound.class);
    }

    @Override
    public void loadNewThreadData(AssetManager manager) {
        Json json = new Json();
        json.addClassTag("chapter", GameData.Chapter.class);
        json.addClassTag("level", GameData.Chapter.Level.class);
        json.addClassTag("color", Color.class);
        gameData = json.fromJson(GameData.class, Gdx.files.internal("gameplay/game_data.json"));

        if (Gdx.files.local(PROGRESS_FILE_NAME).exists()) {
            progressData = json.fromJson(ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
        } else {
            progressData = new ProgressData(gameData);
            json.toJson(progressData, ProgressData.class, Gdx.files.local(PROGRESS_FILE_NAME));
        }

        debug();
        //debugFirstChapter();
    }

    /**
     * Open all levels
     */
    private void debug() {
        progressData.cheatOpen = true;
    }

    private void debugFirstChapter() {
        progressData.chapters[0].cheatOpen = true;
    }

    @Override
    public void show() {
        //Initialize Resources
        gameMusic = new GameMusic("music/menu/menuMusicList.json");
        MySkin defSkin = manager.get("view/ui/default_skin/uiskin.json", MySkin.class);

        //Initialize Stage
        stage = new Stage(new FitViewport(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        //Chapters Table
        Table chaptersTable = new Table();

        //Chapters
        boolean opened = true;
        for (int i = 0; i < gameData.chapters.length; i++) {
            gameData.chapters[i].chapterNumber = i;
            chaptersTable.add(createChapter(gameData.chapters[i],
                    progressData.chapters[i], manager, opened, progressData.cheatOpen))
                    .size(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT).fill();
            opened = opened | progressData.chapters[i].cheatOpen;

            //Check opening of the next chapterNumber (if (scored points >= 70%) ==> open chapterNumber)
            if (opened) {
                opened = progressData.chapters[i].scoredPoints / gameData.chapters[i].maxPoints >= 0.65;
            }
        }

        //ScrollPane
        ScrollPane scrollPane = new ScrollPane(chaptersTable, defSkin);
        scrollPane.setBounds(0f, 0f, VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);
        stage.addActor(scrollPane);
    }

    private Actor createChapter(GameData.Chapter chapterGame, ProgressData.Chapter chapterProgress,
                                AssetManager manager, boolean openedByProgress, boolean openedByGodCheat) {
        //Initialize Resources
        MySkin defSkin = manager.get("view/ui/default_skin/uiskin.json", MySkin.class);
        MySkin mySkin = manager.get("view/ui/my_skin/skin_ui.json", MySkin.class);
        I18NBundle myBundle = manager.get("gameplay/MyBundle", I18NBundle.class);
        final Sound tap = manager.get("sounds/tap.mp3", Sound.class);
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);


        //Main Table
        Table mainTable = new Table();
        mainTable.setSize(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT);


        //Chapter Stack
        Stack chapterStack = new Stack();
        mainTable.add(chapterStack).expandX().fill().row();

        //Image Foreground
        pixmap.setColor(chapterGame.foregroundColor);
        pixmap.fill();
        Image foregroundImage = new Image(new Texture(pixmap));
        foregroundImage.setFillParent(true);
        chapterStack.add(foregroundImage);

        //Chapter Label
        Label chapterLabel = new Label(myBundle.get("chapter")
                .concat(myBundle.get(chapterGame.name)), mySkin, "default");
        chapterLabel.setAlignment(Align.center);
        chapterLabel.setColor(chapterGame.textColor);

        //Chapter Label Container
        Container<Label> chapterContainer = new Container<Label>(chapterLabel);
        chapterContainer.setFillParent(true);
        chapterContainer.pad(15f);
        chapterStack.add(chapterContainer);


        //Levels Table
        Table levelsTable = new Table();
        levelsTable.defaults().expand().pad(20);
        boolean opened = openedByProgress;
        for (int i = 0; i < chapterGame.levels.length; i++) {
            opened = opened | openedByGodCheat
                    | chapterProgress.cheatOpen | chapterProgress.levels[i].cheatOpen;
            chapterGame.levels[i].chapterNumber = chapterGame.chapterNumber;
            chapterGame.levels[i].levelNumber = i;
            levelsTable.add(createLevel(chapterGame.levels[i], chapterProgress.levels[i], manager, opened));

            //if scored points > 0, level will be opened
            if (opened) {
                opened = chapterProgress.levels[i].scoredPoints > 0;
            }
            if ((i + 1) % 4 == 0) {
                levelsTable.row();
            }
        }

        //ScrollPane
        ScrollPane.ScrollPaneStyle scrollPaneStyle = defSkin.get(ScrollPane.ScrollPaneStyle.class);
        pixmap.setColor(chapterGame.backgroundColor);
        pixmap.fill();
        scrollPaneStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));

        ScrollPane levelsScrollPane = new ScrollPane(levelsTable, scrollPaneStyle);
        mainTable.add(levelsScrollPane).expand().fill().row();


        //SecondGroup Stack
        Stack secondGroupStack = new Stack();
        mainTable.add(secondGroupStack).expandX().fill();

        //Image Foreground
        pixmap.setColor(chapterGame.foregroundColor);
        pixmap.fill();
        Image foregroundImage1 = new Image(new Texture(pixmap));
        foregroundImage1.setFillParent(true);
        secondGroupStack.add(foregroundImage1);

        //SecondGroup Table
        Table secondGroupTable = new Table();
        secondGroupTable.setFillParent(true);
        secondGroupStack.add(secondGroupTable);

        //Points Label
        Label pointsLabel = new Label(String.valueOf(chapterProgress.scoredPoints).concat("/")
                .concat(String.valueOf(chapterGame.maxPoints)), mySkin, "default");
        pointsLabel.setColor(chapterGame.textColor);
        secondGroupTable.add(pointsLabel).expandX().pad(5f).padBottom(10f).center();

        //Back Button
        Button backButton = new Button(mySkin, "back");
        backButton.setColor(chapterGame.textColor);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new StartMenu(game));
            }
        });
        secondGroupTable.add(backButton).pad(5f).padBottom(10f);


        pixmap.dispose();
        return mainTable;
    }

    private Actor createLevel(final GameData.Chapter.Level levelGame, ProgressData.Chapter.Level levelProgress,
                              AssetManager manager, boolean opened) {
        //Initialize Resources
        MySkin mySkin = manager.get("view/ui/my_skin/skin_ui.json", MySkin.class);
        MySkin levelsSkin = manager.get("view/ui/levels/levels_skin.json", MySkin.class);
        I18NBundle myBundle = I18NBundle.createBundle(Gdx.files.internal(levelGame.localePath),
                new Locale(LANGUAGE), DEFAULT_I18N_ENCODING);
        final Sound tap = manager.get("sounds/tap.mp3", Sound.class);
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        int maxPoints = levelGame.maxPoints;

        //Main Stack
        Stack mainStack = new Stack();


        //Background
        if (opened) pixmap.setColor(levelGame.foregroundColor);
        else pixmap.setColor(levelGame.backgroundColor);
        pixmap.fill();
        Image backgroundImage = new Image(new Texture(pixmap));
        backgroundImage.setFillParent(true);
        mainStack.add(backgroundImage);


        //Main Table
        Table mainTable = new Table();
        mainTable.defaults().pad(20f);
        mainTable.setFillParent(true);
        mainStack.add(mainTable);

        //Level Label
        Label levelLabel = new Label(myBundle.get(levelGame.name), mySkin, "small");
        mainTable.add(levelLabel).colspan(maxPoints).row();

        //Level Button
        Button levelButton = new Button(levelsSkin, levelGame.type);
        levelButton.setDisabled(!opened);
        levelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new GameLevel(game, levelGame));
            }
        });
        mainTable.add(levelButton).colspan(maxPoints).row();

        //Points
        if (opened) {
            int scoreCount = levelProgress.scoredPoints;
            for (int i = 0; i < maxPoints; i++) {
                Image pointImage = new Image();
                if (scoreCount > 0) {
                    pointImage.setDrawable(levelsSkin, "point");
                } else {
                    pointImage.setDrawable(levelsSkin, "closed_point");
                }
                mainTable.add(pointImage).expandX();
                scoreCount--;
            }
        }


        pixmap.dispose();
        return mainStack;
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
