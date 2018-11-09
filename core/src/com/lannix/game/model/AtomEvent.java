package com.lannix.game.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.lannix.game.utils.basic.NotGoodAtom;
import com.lannix.game.utils.box2d.Box2dBuilder;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.ModelUtils.ATOM_EVENT_COLOR;
import static com.lannix.game.utils.ModelUtils.DENSITY;

public class AtomEvent extends Atom implements NotGoodAtom {

    public AtomEvent() {
    }

    public AtomEvent(GameWorld gameWorld, Vector2 position, Vector2 velocity, float radius, TextureRegion textureRegion) {
        super(gameWorld, position, velocity, radius, textureRegion);
    }

    @Override
    protected Body createAtom(World world, Vector2 position, Vector2 velocity, float radius) {
        return Box2dBuilder.createCircle(world, BodyDef.BodyType.DynamicBody, false,
                false, position, velocity, radius, this, DENSITY, false);
    }

    @Override
    protected void initSprite(Vector2 position, float radius, TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setColor(ATOM_EVENT_COLOR);
        sprite.setOrigin(radius, radius);
        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.setSize(radius * 2f, radius * 2f);
    }
}
