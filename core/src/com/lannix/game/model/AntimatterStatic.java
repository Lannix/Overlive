package com.lannix.game.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.lannix.game.utils.box2d.Box2dBuilder;
import com.lannix.game.view.GameWorld;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.lannix.game.utils.ModelUtils.ANTIMATTER_STATIC;
import static com.lannix.game.utils.ModelUtils.DENSITY;

public class AntimatterStatic extends Antimatter {

    public AntimatterStatic() {
    }

    public AntimatterStatic(GameWorld gameWorld, Vector2 position, Vector2 velocity, float radius, TextureRegion textureRegion) {
        super(gameWorld, position, velocity, radius, textureRegion);
    }

    @Override
    protected void initSprite(Vector2 position, float radius, TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setColor(ANTIMATTER_STATIC);
        sprite.setOrigin(radius, radius);
        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.setSize(radius * 2f, radius * 2f);
    }

    @Override
    protected Body createAtom(World world, Vector2 position, Vector2 velocity, float radius) {
        return Box2dBuilder.createCircle(world, BodyDef.BodyType.StaticBody, false,
                false, position, velocity, radius, this, DENSITY, false);
    }

    @Override
    protected Body createRandPosAtom(World world, float atomRad, Vector2 pos, float minRad, float maxRad) {
        return createAtom(world, pos.add(new Vector2(random(-1f, 1f), random(-1f, 1f))
                .setLength(random(maxRad, maxRad))), null, atomRad);
    }
}
