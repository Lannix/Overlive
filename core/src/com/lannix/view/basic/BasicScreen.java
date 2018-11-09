package com.lannix.view.basic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;

public abstract class BasicScreen extends ScreenAdapter implements Loader {

    protected final Game game;

    public BasicScreen(Game game) {
        this.game = game;
    }

    @Override
    public void hide() {
        dispose();
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

    public Game getGame() {
        return game;
    }
}
