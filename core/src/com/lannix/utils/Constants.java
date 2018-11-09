package com.lannix.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Constants {

    //UI
    public static final String TITLE = "Overlive";
    public static final int DEFAULT_TEXT_SIZE = 45;
    public static volatile float VIRTUAL_SCREEN_WIDTH = 1024;
    public static volatile float VIRTUAL_SCREEN_HEIGHT = 576;

    public static final float BACKGROUND_COLOR_RED = 0.2f;
    public static final float BACKGROUND_COLOR_GREEN = 0.2f;
    public static final float BACKGROUND_COLOR_BLUE = 0.2f;
    public static final float BACKGROUND_COLOR_ALPHA = 1f;

    //i18n
    public static final String DEFAULT_I18N_ENCODING = "windows-1251";
    public static final String RU = "Ru";
    public static final String EN = "En";
    public static final String[] LANGUAGES = new String[]{RU, EN};


    public static void init() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            VIRTUAL_SCREEN_WIDTH = 1280;
            VIRTUAL_SCREEN_HEIGHT = 720;
        } else if (Gdx.app.getType() == Application.ApplicationType.Android) {
            VIRTUAL_SCREEN_WIDTH = 1024;
            VIRTUAL_SCREEN_HEIGHT = 576;
        }
    }

    public static void update(float width, float height) {
        VIRTUAL_SCREEN_WIDTH = VIRTUAL_SCREEN_HEIGHT * width / height;
    }
}
