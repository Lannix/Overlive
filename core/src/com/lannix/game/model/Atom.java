package com.lannix.game.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.lannix.game.utils.box2d.Box2dBuilder;
import com.lannix.game.utils.box2d.UserData;
import com.lannix.game.view.GameWorld;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.badlogic.gdx.math.MathUtils.randomTriangular;
import static com.lannix.game.utils.ModelUtils.ATOM_COLOR;
import static com.lannix.game.utils.ModelUtils.DENSITY;
import static com.lannix.game.utils.ModelUtils.MAX_RANDOM_VELOCITY;

public class Atom implements UserData {

    protected boolean visible = false;
    protected Sprite sprite;
    protected Body body;
    protected GameWorld gameWorld;

    public Atom() {
    }

    public Atom(GameWorld gameWorld, Vector2 position, Vector2 velocity, float radius, TextureRegion textureRegion) {
        init(gameWorld, position, velocity, radius, textureRegion);
    }

    /**
     * NOTE: This is without batch.begin() and bath.end()
     **/
    @Override
    public void act(float delta) {
        if (visible) {
            sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
            sprite.setOriginBasedPosition(getBody().getPosition().x, getBody().getPosition().y);
        }
    }

    @Override
    public void draw() {
        if (visible) {
            sprite.draw(gameWorld.getBatch());
        }
    }

    @Override
    public void reset() {
        if (body != null) {
            if (body.isActive()) {
                body.setActive(false);
                body.getWorld().destroyBody(body);
            }
            body = null;
        }
        sprite = null;
        gameWorld = null;
        visible = false;
    }

    public void init(GameWorld gameWorld, Vector2 position, Vector2 velocity, float radius, TextureRegion textureRegion) {
        this.gameWorld = gameWorld;
        body = createAtom(gameWorld.getWorld(), position, velocity, radius);
        visible = true;

        initSprite(position, radius, textureRegion);
    }

    public void init(GameWorld gameWorld, float radius, TextureRegion textureRegion, Vector2 pos, float minRad, float maxRad) {
        this.gameWorld = gameWorld;
        body = createRandPosAtom(gameWorld.getWorld(), radius, pos, minRad, maxRad);
        visible = true;

        initSprite(body.getPosition(), radius, textureRegion);
    }

    protected void initSprite(Vector2 position, float radius, TextureRegion textureRegion) {
        sprite = new Sprite(textureRegion);
        sprite.setColor(ATOM_COLOR);
        sprite.setOrigin(radius, radius);
        sprite.setOriginBasedPosition(position.x, position.y);
        sprite.setSize(radius * 2f, radius * 2f);
    }

    protected Body createAtom(World world, Vector2 position, Vector2 velocity, float radius) {
        return Box2dBuilder.createCircle(world, BodyDef.BodyType.DynamicBody, false,
                false, position, velocity, radius, this, DENSITY, false);
    }

    protected Body createRandPosAtom(World world, float atomRad, Vector2 pos, float minRad, float maxRad) {
        return createAtom(world, pos.add(new Vector2(random(-1f, 1f), random(-1f, 1f))
                .setLength(random(minRad, maxRad))), new Vector2(randomTriangular(),
                randomTriangular()).setLength(random(MAX_RANDOM_VELOCITY)), atomRad);
    }

    public Body getBody() {
        return body;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
