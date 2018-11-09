package com.lannix.view.main_menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.lannix.utils.GameMusic;
import com.lannix.utils.skin.MySkin;
import com.lannix.utils.skin.MySkinLoader;
import com.lannix.view.basic.BasicScreen;

import java.util.Locale;

import static com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888;
import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_HEIGHT;
import static com.lannix.utils.Constants.VIRTUAL_SCREEN_WIDTH;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.Settings.PLAY_MUSIC;

public class InfoScreen extends BasicScreen {

    private Stage stage;
    private GameMusic gameMusic;
    private AssetManager manager;

    public InfoScreen(Game game) {
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
    }

    @Override
    public void onDataLoaded(AssetManager manager) {
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


        //MainTable
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);


        //InfoTable
        Table infoTable = new Table();
        infoTable.setFillParent(true);

        //MainScrollPane
        ScrollPane mainScrollPane = new ScrollPane(infoTable, defSkin);
        mainScrollPane.setFillParent(true);
        mainTable.add(mainScrollPane).fill().row();


        //BackPanel
        Stack backStack = new Stack();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor();

        Image image = new Image(new Texture());
        backStack.add();
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
        manager.dispose();
        stage.dispose();
        gameMusic.dispose();
    }
}
