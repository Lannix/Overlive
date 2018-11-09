package com.lannix.game.utils;

public class GameConstants {

    //Game
    public static final float ONE_STAR = 0.6f;
    public static final float TWO_STARS = 0.75f;
    public static final float THREE_STARS = 0.9f;

    //box2d
    public static final float TIME_STEP = 1 / 60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    public static volatile float CAMERA_SCALE = 1f;
    public static volatile float BOX2D_HEIGHT = 30f;
    public static volatile float BOX2D_WIDTH = 60f; //driving dimension

    public static void initBox2dViewport(float width, float height, float scale) {
        BOX2D_WIDTH = BOX2D_HEIGHT * width / height;
        CAMERA_SCALE = scale;
    }

    public static void updateViewport(float scale) {
        CAMERA_SCALE = scale;
    }

    public static float getBox2dWidth() {
        return BOX2D_WIDTH * CAMERA_SCALE;
    }

    public static float getBox2dHeight() {
        return BOX2D_HEIGHT * CAMERA_SCALE;
    }
}
