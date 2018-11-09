package com.lannix.utils.skin;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MySkinLoader extends AsynchronousAssetLoader<MySkin, MySkinLoader.MySkinParameter>  {

    public MySkinLoader (FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, MySkinLoader.MySkinParameter parameter) {
        Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
        if (parameter == null || parameter.textureAtlasPath == null)
            deps.add(new AssetDescriptor(file.pathWithoutExtension() + ".atlas", TextureAtlas.class));
        else if (parameter.textureAtlasPath != null) deps.add(new AssetDescriptor(parameter.textureAtlasPath, TextureAtlas.class));
        return deps;
    }

    @Override
    public void loadAsync (AssetManager manager, String fileName, FileHandle file, MySkinLoader.MySkinParameter parameter) {
    }

    @Override
    public MySkin loadSync (AssetManager manager, String fileName, FileHandle file, MySkinLoader.MySkinParameter parameter) {
        String textureAtlasPath = file.pathWithoutExtension() + ".atlas";
        ObjectMap<String, Object> resources = null;
        if (parameter != null) {
            if (parameter.textureAtlasPath != null){
                textureAtlasPath = parameter.textureAtlasPath;
            }
            if (parameter.resources != null){
                resources = parameter.resources;
            }
        }
        TextureAtlas atlas = manager.get(textureAtlasPath, TextureAtlas.class);
        MySkin skin = new MySkin(atlas);
        if (resources != null) {
            for (ObjectMap.Entry<String, Object> entry : resources.entries()) {
                skin.add(entry.key, entry.value);
            }
        }
        skin.load(file);
        return skin;
    }

    static public class MySkinParameter extends AssetLoaderParameters<MySkin> {
        public final String textureAtlasPath;
        public final ObjectMap<String, Object> resources;

        public MySkinParameter() {
            this(null, null);
        }

        public MySkinParameter(ObjectMap<String, Object> resources){
            this(null, resources);
        }

        public MySkinParameter(String textureAtlasPath) {
            this(textureAtlasPath, null);
        }

        public MySkinParameter(String textureAtlasPath, ObjectMap<String, Object> resources) {
            this.textureAtlasPath = textureAtlasPath;
            this.resources = resources;
        }
    }
}
