package com.lannix.view.main_menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lannix.game.view.GameLevel;
import com.lannix.utils.GameMusic;
import com.lannix.utils.Settings;
import com.lannix.utils.skin.MySkin;
import com.lannix.utils.skin.MySkinLoader;
import com.lannix.view.basic.BasicScreen;

import java.util.Locale;

import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.LANGUAGES;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_HEIGHT;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_WIDTH;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.Settings.LAST_LEVEL_DATA;
import static com.lannix.utils.Settings.PLAYER_NAME;
import static com.lannix.utils.Settings.PLAY_MUSIC;
import static com.lannix.view.basic.ScreenLoader.swapScreenWithLoader;

public class StartMenu extends BasicScreen {

    private Stage stage;
    private GameMusic gameMusic;
    private AssetManager manager;

    public StartMenu(Game game) {
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

        manager.setLoader(I18NBundle.class, new I18NBundleLoader(new InternalFileHandleResolver()));
        manager.load("gameplay/MyBundle", I18NBundle.class,
                new I18NBundleLoader.I18NBundleParameter(new Locale(LANGUAGE), DEFAULT_I18N_ENCODING));

        manager.load("sounds/tap.mp3", Sound.class);
    }

    @Override
    public void loadNewThreadData(AssetManager manager) {
        Settings.loadNewLastLevel();
    }

    @Override
    public void show() {
        float w = VIRTUAL_SCREEN_WIDTH / 100f;
        float h = VIRTUAL_SCREEN_HEIGHT / 100f;

        //Initialize Resources
        gameMusic = new GameMusic("music/menu/menuMusicList.json");
        MySkin defSkin = manager.get("view/ui/default_skin/uiskin.json", MySkin.class);
        MySkin mySkin = manager.get("view/ui/my_skin/skin_ui.json", MySkin.class);
        I18NBundle myBundle = manager.get("gameplay/MyBundle", I18NBundle.class);
        final Sound tap = manager.get("sounds/tap.mp3", Sound.class);

        stage = new Stage(new FitViewport(VIRTUAL_SCREEN_WIDTH, VIRTUAL_SCREEN_HEIGHT));
        Gdx.input.setInputProcessor(stage);


        //Atom Buttons
        Button atom = new Button(mySkin, "atom");
        atom.setPosition(w * 15, h * 68, Align.center);
        atom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
            }
        });
        stage.addActor(atom);


        Button atom1 = new Button(mySkin, "atom_1");
        atom1.setPosition(w * 80, h * 55, Align.center);
        atom1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
            }
        });
        stage.addActor(atom1);


        Button atom2 = new Button(mySkin, "atom_2");
        atom2.setPosition(w * 30, h * 24, Align.center);
        atom2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
            }
        });
        stage.addActor(atom2);


        //Main Table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        mainTable.defaults().pad(5f);
        stage.addActor(mainTable);


        //Title Label
        Label titleLabel = new Label(myBundle.get("app_name"), mySkin, "big");
        mainTable.add(titleLabel).padBottom(50f).row();


        //Player Name TextField
        TextField playerNameField = new TextField(PLAYER_NAME.equals("") ? myBundle.get("player_name") : PLAYER_NAME, defSkin, "default");
        playerNameField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
            }
        });
        playerNameField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                PLAYER_NAME = textField.getText();
            }
        });
        playerNameField.setMessageText(myBundle.get("write_name"));
        playerNameField.setMaxLength(10);
        mainTable.add(playerNameField).row();


        //Continue Last Level Button
        TextButton continueButton = new TextButton(myBundle.get("continue"), mySkin, "default");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                if (LAST_LEVEL_DATA != null)
                    swapScreenWithLoader(new GameLevel(game, LAST_LEVEL_DATA));
            }
        });
        mainTable.add(continueButton).row();


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


        //Exit Button
        TextButton exitButton = new TextButton(myBundle.get("exit"), mySkin, "default");
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                Gdx.app.exit();
            }
        });
        mainTable.add(exitButton).row();


        //Music CheckBox
        CheckBox musicCheckBox = new CheckBox(myBundle.get("music"), mySkin, "default");
        musicCheckBox.setPosition(20f, 20f);
        musicCheckBox.setChecked(PLAY_MUSIC);
        musicCheckBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PLAY_MUSIC = !PLAY_MUSIC;
                if (PLAY_MUSIC) tap.play();
            }
        });
        stage.addActor(musicCheckBox);


        //Horizontal Group
        HorizontalGroup subMainGroup = new HorizontalGroup();
        subMainGroup.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        subMainGroup.align(Align.bottomRight);
        subMainGroup.padBottom(20f);
        subMainGroup.padRight(20f);
        subMainGroup.space(10f);
        stage.addActor(subMainGroup);


        //Info Button
        Button infoButton = new Button(mySkin, "info");
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
                swapScreenWithLoader(new InfoScreen(StartMenu.this.game));
            }
        });
        subMainGroup.addActor(infoButton);


        //Language SelectBox
        final SelectBox<String> languageBox = new SelectBox<String>(defSkin);
        languageBox.setItems(LANGUAGES);
        languageBox.setSelected(LANGUAGE);
        languageBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LANGUAGE = languageBox.getSelected();
                swapScreenWithLoader(new StartMenu(game));
            }
        });
        languageBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PLAY_MUSIC) tap.play();
            }
        });
        subMainGroup.addActor(languageBox);
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
