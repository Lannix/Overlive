package com.lannix.game.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lannix.game.utils.basic.Background;

public class DynamicBackground implements Background {

    private Sprite background;

    public DynamicBackground(TextureRegion textureRegion) {
        background = new Sprite(textureRegion);
        background.setOriginCenter();
    }

    @Override
    public void render(Batch batch, Vector3 cameraPos, float width, float height) {
        background.setSize(background.getWidth() * height / background.getHeight(), height);

        float camX = cameraPos.x;
        float camY = cameraPos.y;
        float viewportBottom = camY - height / 2f;
        float viewportLeft = camX - width / 2f;

        if (new Vector2(background.getX(), background.getY()).dst2(viewportLeft, viewportBottom) >
                width * width + height * height * 9f) {
            background.setOriginBasedPosition(camX, camY);
        }

        if (background.getX() > viewportLeft) {
            background.translate(-background.getWidth(), 0f);
        }
        if (background.getY() > viewportBottom) {
            background.translate(0f, -background.getHeight());
        }
        if (background.getX() + background.getWidth() < viewportLeft) {
            background.translate(background.getWidth(), 0f);
        }
        if (background.getY() + background.getHeight() < viewportBottom) {
            background.translate(0f, background.getHeight());
        }

        background.draw(batch);
        background.translate(background.getWidth(), 0f);
        background.draw(batch);
        background.translate(0f, background.getHeight());
        background.draw(batch);
        background.translate(-background.getWidth(), 0f);
        background.draw(batch);
        background.translate(0f, -background.getHeight());
    }
}
