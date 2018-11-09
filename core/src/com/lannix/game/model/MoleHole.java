package com.lannix.game.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.lannix.game.utils.box2d.Box2dBuilder;
import com.lannix.game.utils.box2d.UserData;
import com.lannix.game.view.GameWorld;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.lannix.game.utils.ModelUtils.DENSITY;
import static com.lannix.game.utils.ModelUtils.MAX_RAND_RADIUS;
import static com.lannix.game.utils.ModelUtils.MIN_RAND_RADIUS;

public class MoleHole implements Pool.Poolable, UserData {

    private boolean visible = true;
    private final float angleVel = -12f;
    private Body body;
    private Sprite sprite;
    private Vector2 position;
    private GameWorld gameWorld;

    public MoleHole(GameWorld gameWorld, float radius, TextureRegion textureRegion) {
        init(gameWorld, radius, textureRegion);
    }

    public MoleHole(GameWorld gameWorld, Vector2 position, float radius, TextureRegion textureRegion) {
        init(gameWorld, position, radius, textureRegion);
    }

    @Override
    public void act(float delta) {
        if (visible) {
            sprite.rotate(angleVel * delta);
            sprite.setOriginBasedPosition(getBody().getPosition().x, getBody().getPosition().y);
        }
    }

    /**
     * NOTE: This is without batch.begin() and bath.end()
     **/
    @Override
    public void draw() {
        if (visible) {
            sprite.draw(gameWorld.getBatch());
        }
    }

    public void reset() {
        if (body != null) {
            if (body.isActive()) {
                body.setActive(false);
                body.getWorld().destroyBody(body);
            }
            body = null;
        }
        gameWorld = null;
        visible = false;
    }

    public void init(GameWorld gameWorld, float radius, TextureRegion textureRegion) {
        this.gameWorld = gameWorld;
        body = createRandPosAtom(gameWorld.getWorld(), radius);
        position = body.getPosition();

        initSprite(position, radius, textureRegion);
    }

    public void init(GameWorld gameWorld, Vector2 position, float radius, TextureRegion textureRegion) {
        this.gameWorld = gameWorld;
        body = createBlackHole(gameWorld.getWorld(), position, radius);
        this.position = body.getPosition();

        initSprite(position, radius, textureRegion);
    }

    private void initSprite(Vector2 position, float radius, TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setColor(Color.WHITE);
        sprite.setOrigin(radius, radius);
        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.setSize(radius * 2f, radius * 2f);
    }

    private Body createBlackHole(World world, Vector2 position, float radius) {
        return Box2dBuilder.createCircle(world, BodyDef.BodyType.StaticBody, false,
                true, position, null, radius, this, DENSITY, true);
    }

    private Body createRandPosAtom(World world, float radius) {
        return createBlackHole(world, new Vector2(random(-1f, 1f), random(-1f, 1f))
                .setLength(random(MIN_RAND_RADIUS, MAX_RAND_RADIUS)), radius);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Body getBody() {
        return body;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
