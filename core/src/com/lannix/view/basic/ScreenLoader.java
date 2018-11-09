package com.lannix.view.basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Locale;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.lannix.utils.Constants.DEFAULT_I18N_ENCODING;
import static com.lannix.utils.Constants.DEFAULT_TEXT_SIZE;
import static com.lannix.utils.Settings.LANGUAGE;
import static com.lannix.utils.skin.MySkin.DEFAULT_FONT_CHARS;

public class ScreenLoader extends BasicScreen {

    private Stage stage;
    private AssetManager manager;
    private volatile boolean loaded = false;

    public ScreenLoader(Game game) {
        super(game);
        manager = new AssetManager();
        loadAssetManagerData(manager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadNewThreadData(manager);
                loaded = true;
            }
        }).start();
    }

    @Override
    public void loadAssetManagerData(AssetManager manager) {
    }

    @Override
    public void loadNewThreadData(AssetManager manager) {
    }

    @Override
    public void onDataLoaded(AssetManager manager) {
    }

    @Override
    public void show() {
        final I18NBundle myBundle = I18NBundle.createBundle(Gdx.files.internal("gameplay/MyBundle"), new Locale(LANGUAGE), DEFAULT_I18N_ENCODING);

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));


        //Download Label
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("view/ui/my_skin/Kankin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = DEFAULT_FONT_CHARS;
        parameter.color = Color.WHITE;
        parameter.size = DEFAULT_TEXT_SIZE;

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = generator.generateFont(parameter);
        labelStyle.fontColor = Color.WHITE;
        generator.dispose();

        final Label downloadLabel = new Label(myBundle.format("downloading", (int) (manager.getProgress() * 100f)), labelStyle);
        downloadLabel.setPosition(25f, 25f);
        downloadLabel.addAction(forever(run(new Runnable() {
            @Override
            public void run() {
                downloadLabel.setText(myBundle.format("downloading", (int) (manager.getProgress() * 100f)));
            }
        })));
        stage.addActor(downloadLabel);


        //Progress Image
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        final Image progressImage = new Image(new Texture(pixmap));
        progressImage.setSize(0f, 10f);
        progressImage.setPosition(0f, 10f);
        progressImage.addAction(forever(run(new Runnable() {
            @Override
            public void run() {
                progressImage.setSize(stage.getWidth() * manager.getProgress(), 10f);
            }
        })));
        stage.addActor(progressImage);
        pixmap.dispose();


        //Download Image
        Texture downloadTexture = new Texture("view/ui/download.png");
        downloadTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Image downloadImage = new Image(downloadTexture);
        downloadImage.setOrigin(Align.center);
        downloadImage.setAlign(Align.center);
        downloadImage.addAction(forever(rotateBy(360f, 0.6f)));

        Container<Image> downloadImageContainer = new Container<Image>();
        downloadImageContainer.setBounds(0f, 0f, stage.getWidth(), stage.getHeight());
        downloadImageContainer.setActor(downloadImage);
        stage.addActor(downloadImageContainer);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (manager.update() & loaded) onDataLoaded(manager);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public static void swapScreenWithLoader(final BasicScreen screen) {
        Game game = screen.getGame();
        game.setScreen(new ScreenLoader(game) {

            @Override
            public void loadAssetManagerData(AssetManager manager) {
                screen.loadAssetManagerData(manager);
            }

            @Override
            public void loadNewThreadData(AssetManager manager) {
                screen.loadNewThreadData(manager);
            }

            @Override
            public void onDataLoaded(AssetManager manager) {
                screen.onDataLoaded(manager);
                game.setScreen(screen);
            }
        });
    }
}
