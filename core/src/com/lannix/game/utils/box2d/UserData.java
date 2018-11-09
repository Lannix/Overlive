package com.lannix.game.utils.box2d;

import com.badlogic.gdx.utils.Pool;

public interface UserData extends Pool.Poolable {

    void act(float delta);
    void draw();
    void reset();
}
