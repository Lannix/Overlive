package com.lannix.game.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lannix.game.utils.GameData;
import com.lannix.game.utils.LevelBuilder;
import com.lannix.utils.GameMusic;
import com.lannix.utils.skin.MySkin;
import com.lannix.utils.skin.MySkinLoader;
import com.lannix.view.basic.BasicScreen;
import com.lannix.view.main_menu.Levels;
import com.lannix.view.main_menu.StartMenu;

import java.util.Locale;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.lannix.game.utils.ProgressData.saveNewScore;
import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_HEIGHT;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_WIDTH;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.Settings.PLAY_MUSIC;
import static com.lannix.utils.Settings.saveNewLastLevel;
import static com.lannix.view.basic.ScreenLoader.swapScreenWithLoader;

public class GameLevel extends BasicScreen {

    private Stage stage;
    private GameMusic gameMusic;
    private AssetManager manager;
    private GameWorld gameWorld;
    private LevelBuilder levelBuilder;
    private GameData.Chapter.Level levelData;

    public GameLevel(Game game, GameData.Chapter.Level levelData) {
        super(game);
        this.levelData = levelData;
        stage = new Stage(new FitViewport(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT));
        gameWorld = new GameWorld(stage.getBatch(), levelData, this);
    }

    @Override
    public void loadAssetManagerData(AssetManager manager) {
        this.manager = manager;

        manager.setLoader(MySkin.class, new MySkinLoader(new InternalFileHandleResolver()));
        manager.load("view/ui/default_skin/uiskin.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/default_skin/uiskin.atlas"));
        manager.load("view/ui/my_skin/skin_ui.json", MySkin.class,
                new MySkinLoader.MySkinParameter("view/ui/my_skin/skin_ui.atlas"));

        manager.setLoader(I18NBundle.class, new I18NBundleLoader(new InternalFileHandleResolver()));
        manager.load("gameplay/MyBundle", I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(new Locale(LANGUAGE), DEFAULT_I18N_ENCODING));
        manager.load(levelData.localePath, I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(new Locale(LANGUAGE), DEFAULT_I18N_ENCODING));

        manager.load("sounds/tap.mp3", Sound.class);

        gameWorld.loadAssetManagerData(manager);
    }

    @Override
    public void loadNewThreadData(AssetManager manager) {
        gameWorld.loadNewThreadData(manager);
        saveNewLastLevel(levelData);
    }

    @Override
    public void onDataLoaded(AssetManager manager) {
        gameWorld.onDataLoaded(manager);

        levelBuilder = new LevelBuilder(levelData);
        levelBuilder.createLevel();
    }

    @Override
    public void show() {
        gameWorld.show();
        float w = VIRTUAL_SCREEN_WIDTH / 100f;
        float h = VIRTUAL_SCREEN_HEIGHT / 100f;

        //Initialize Resources
        gameMusic = new GameMusic("music/game/gameMusicList.json");
        MySkin defSkin = manager.get("view/ui/default_skin/uiskin.json", MySkin.class);
        MySkin mySkin = manager.get("view/ui/my_skin/skin_ui.json", MySkin.class);
        I18NBundle basicBundle = manager.get("gameplay/MyBundle", I18NBundle.class);
        I18NBundle levelBundle = manager.get(levelData.localePath, I18NBundle.class);
        final Sound tap = manager.get("sounds/tap.mp3", Sound.class);
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);


        //Initialize InputMultiplexer
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(gameWorld.getMultiplexer());
        Gdx.input.setInputProcessor(multiplexer);



        /***************************UI******************************/

        //Pause Menu Table
        pixmap.setColor(levelData.backgroundColor);
        pixmap.fill();
        final Table pauseMenuTable = new Table();
        pauseMenuTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        pauseMenuTable.center();
        pauseMenuTable.setVisible(false);
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.defaults().pad(5f);
        stage.addActor(pauseMenuTable);


        //Second Table
        final Table submain = new Table();
        submain.setFillParent(true);
        submain.left().top().pad(10f);
        submain.defaults().space(15f);
        stage.addActor(submain);

        //Third Table
        final Table levelTextTable = new Table();
        levelTextTable.setFillParent(true);
        levelTextTable.top().right().pad(10f);
        stage.addActor(levelTextTable);

        //Pause Button
        Button pauseButton = new Button(mySkin, "pause");
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                gameWorld.setStepWorld(false);
                pauseMenuTable.setVisible(true);
                submain.setVisible(false);
            }
        });
        submain.add(pauseButton);

        //Direction Image
        final Image directionImage = new Image(mySkin.getDrawable("direction"));
        directionImage.setOrigin(Align.center);
        directionImage.addAction(forever(run(new Runnable() {
            @Override
            public void run() {
                directionImage.setRotation(gameWorld.getTargetDirAngleHor());
            }
        })));
        submain.add(directionImage).size(directionImage.getWidth(),
                directionImage.getHeight());

        //LevelTextTitle Stack
        final Stack levelTextTitleStack = new Stack();
        levelTextTable.add(levelTextTitleStack).fill().padBottom(0f).row();

        //LevelTextTitle Label Background
        pixmap.setColor(levelData.backgroundColor);
        pixmap.fill();
        Image levelTextTitleImage = new Image(new Texture(pixmap));
        levelTextTitleImage.setFillParent(true);
        levelTextTitleStack.add(levelTextTitleImage);

        //LevelText Label Container
        Container<Label> levelTextTitleLabelContainer = new Container<Label>();
        levelTextTitleLabelContainer.padLeft(15f).padRight(15f).padTop(5f).padBottom(5f);
        levelTextTitleStack.add(levelTextTitleLabelContainer);

        //Level Title Text Label
        Label levelTextTitleLabel = new Label(levelBundle.get(levelData.type), mySkin, "small");
        levelTextTitleLabel.setAlignment(Align.center);
        levelTextTitleLabelContainer.setActor(levelTextTitleLabel);

        //LevelText Stack
        final Stack levelTextStack = new Stack();
        levelTextTable.add(levelTextStack).fill();

        //LevelTextTitle Label Background
        pixmap.setColor(levelData.foregroundColor);
        pixmap.fill();
        Image levelTextImage = new Image(new Texture(pixmap));
        levelTextImage.setFillParent(true);
        levelTextStack.add(levelTextImage);

        //LevelText Label Container
        Container<Label> levelTextLabelContainer = new Container<Label>();
        levelTextLabelContainer.padLeft(15f).padRight(15f).padTop(5f).padBottom(5f);
        levelTextStack.add(levelTextLabelContainer);

        //LevelText Label
        final LevelBuilder.LevelText levelText = levelBuilder.createLevelText(levelBundle, levelData.type, gameWorld);
        final Label levelTextLabel = new Label("", mySkin, "very_small");
        levelTextLabel.addAction(forever(run(new Runnable() {
            @Override
            public void run() {
                levelTextLabel.setText(levelText.createText());
            }
        })));
        levelTextLabel.setAlignment(Align.left);
        levelTextLabelContainer.setActor(levelTextLabel);


        //Level Title Label
        Label levelTitleLabel = new Label(levelBundle.get(levelData.name), mySkin, "big");
        pauseMenuTable.add(levelTitleLabel).padBottom(25f).row();

        //Continue Button
        TextButton continueButton = new TextButton(basicBundle.get("continue"), mySkin, "default");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                gameWorld.setStepWorld(true);
                pauseMenuTable.setVisible(false);
                submain.setVisible(true);
            }
        });
        pauseMenuTable.add(continueButton).row();

        //Levels Button
        TextButton levelsButton = new TextButton(basicBundle.get("levels"), mySkin, "default");
        levelsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new Levels(game));
            }
        });
        pauseMenuTable.add(levelsButton).row();

        //Levels Button
        TextButton menuButton = new TextButton(basicBundle.get("menu"), mySkin, "default");
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new StartMenu(game));
            }
        });
        pauseMenuTable.add(menuButton);


        //Dialog Table
        final LevelBuilder.Dialog dialog = levelBuilder.createDialog(levelBundle);
        if (dialog != null) {
            final int maxImg = dialog.imageIndexes.length - 1, maxText = dialog.dialogs.length - 1;

            //MainTable
            pixmap.setColor(levelData.backgroundColor);
            pixmap.fill();
            final Table dialogTable = new Table();
            dialogTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
            dialogTable.setBounds(0f, 0f, w * 85, h * 24f);
            dialogTable.setTouchable(Touchable.enabled);
            stage.addActor(dialogTable);

            //Dialog Image
            final Image dialogImage = new Image(gameWorld.getGameAtlas().createSprite(dialog
                    .imagesFromGameAtlas[0], dialog.imageIndexes[0]));
            dialogImage.setSize(128, 128);
            dialogTable.add(dialogImage).size(dialogImage.getWidth(), dialogImage.getHeight()).pad(15f);

            //Text Table
            Table textTable = new Table();
            dialogTable.add(textTable).expand().fill().pad(5f);

            //Dialog Text Label
            final Label dialogText = new Label(dialog.dialogs[0], mySkin, "small");
            dialogText.setAlignment(Align.left);

            //Text ScrollPaneScrollPane.ScrollPaneStyle scrollPaneStyle = defSkin.get(ScrollPane.ScrollPaneStyle.class);
            pixmap.setColor(levelData.foregroundColor);
            pixmap.fill();
            final ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
            scrollPaneStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));

            final ScrollPane textScrollPane = new ScrollPane(dialogText, scrollPaneStyle);
            textTable.add(textScrollPane).colspan(3).expand().fill().space(5f).row();

            //Prev Button
            final int curPos[] = new int[]{0};
            TextButton prevButton = new TextButton(basicBundle.get("prev"), mySkin, "very_small");
            prevButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (PLAY_MUSIC) tap.play();
                    curPos[0] = (curPos[0] == 0 ? 0 : curPos[0] - 1);
                    dialogImage.setDrawable(new TextureRegionDrawable(gameWorld.getGameAtlas()
                            .findRegion(dialog.imagesFromGameAtlas[curPos[0]], dialog.imageIndexes[curPos[0]])));
                    dialogImage.setSize(128, 128);
                    dialogText.setText(dialog.dialogs[curPos[0]]);
                }
            });
            textTable.add(prevButton).padBottom(2f).left();

            //Next Button
            TextButton nextButton = new TextButton(basicBundle.get("next"), mySkin, "very_small");
            nextButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (PLAY_MUSIC) tap.play();
                    if (curPos[0] + 1 == maxText) {
                        stage.unfocus(textScrollPane);
                        dialogTable.setVisible(false);
                        return;
                    }
                    curPos[0] = (curPos[0] == maxText ? maxText : curPos[0] + 1);
                    dialogImage.setDrawable(new TextureRegionDrawable(gameWorld.getGameAtlas()
                            .findRegion(dialog.imagesFromGameAtlas[curPos[0]], dialog.imageIndexes[curPos[0]])));
                    dialogText.setText(dialog.dialogs[curPos[0]]);
                }
            });
            textTable.add(nextButton).padBottom(2f).left();

            //Fade TextButton
            TextButton fadeButton = new TextButton(basicBundle.get("fade"), mySkin, "very_small");
            fadeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (PLAY_MUSIC) tap.play();
                    stage.unfocus(textScrollPane);
                    dialogTable.setVisible(false);
                }
            });
            textTable.add(fadeButton).padBottom(2f).right();
        }


        pixmap.dispose();
        levelBuilder = null;
    }

    @Override
    public void render(float delta) {
        gameMusic.act(PLAY_MUSIC);
        gameWorld.render(delta);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameWorld.resize(width, height);
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameWorld.dispose();
        manager.dispose();
        gameMusic.dispose();
    }

    public void gameOver(final GameData.Chapter.Level levelData, final int points, final int maxPoints) {
        saveNewScore(levelData.chapterNumber, levelData.levelNumber, points);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                swapScreenWithLoader(new GameOver(game, levelData, points, maxPoints));
            }
        });
    }
}
