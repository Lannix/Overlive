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

import static com.badlogic.gdx.math.MathUtils.randomTriangular;
import static com.lannix.game.utils.ModelUtils.DENSITY;
import static com.lannix.game.utils.ModelUtils.RADIATION;
import static com.lannix.game.utils.ModelUtils.RADIATION_RADIUS;
import static com.lannix.game.utils.ModelUtils.SMALL_BLAST_POWER;

public class Radiation extends Atom implements NotGoodAtom {

    public Radiation() {
    }

    public Radiation(GameWorld gameWorld, Vector2 position, Vector2 velocity, float radius, TextureRegion textureRegion) {
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
        sprite.setColor(RADIATION);
        sprite.setOrigin(radius, radius);
        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.setSize(radius * 2f, radius * 2f);
    }

    public void prune() {
        gameWorld.addRunnable(new Runnable() {
            @Override
            public void run() {
                float radius = body.getFixtureList().first().getShape().getRadius();
                Vector2 pos = body.getPosition();
                Vector2 vel = body.getLinearVelocity();

                if (radius >= RADIATION_RADIUS / 4f) {
                    Vector2 offset = new Vector2(randomTriangular(), randomTriangular()).setLength(radius * 1.1f);

                    gameWorld.createRadiation(pos.cpy().add(offset), vel, radius / 2f);
                    gameWorld.createRadiation(pos.cpy().add(offset.scl(-1f)), vel, radius / 2f);

                    Box2dBuilder.applySquareBlast(gameWorld, pos, RADIATION_RADIUS * 5, SMALL_BLAST_POWER, false);
                }

                gameWorld.freeAtom(Radiation.this, false);
            }
        });
    }
}
