package com.lannix.view.basic;

import com.badlogic.gdx.assets.AssetManager;

public interface Loader {

    void loadAssetManagerData(AssetManager manager);

    /**
     * Don't use it, if you load OpenGl data.
     */
    void loadNewThreadData(AssetManager manager);

    void onDataLoaded(AssetManager manager);
}
