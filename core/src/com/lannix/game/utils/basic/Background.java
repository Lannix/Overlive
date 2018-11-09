package com.lannix.game.utils.basic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

public interface Background {

    void render(Batch batch, Vector3 cameraPos, float width, float height);
}
