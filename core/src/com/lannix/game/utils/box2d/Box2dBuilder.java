package com.lannix.game.utils.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.lannix.game.model.Atom;
import com.lannix.game.view.GameWorld;

public class Box2dBuilder {

    public static Body createCircle(World world, BodyDef.BodyType type, boolean allowSleep,
                                    boolean fixedRotation, Vector2 position, Vector2 velocity,
                                    float radius, UserData data, float density, boolean isSensor) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.allowSleep = allowSleep;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.position.set(position);

        Body body;
        body = world.createBody(bodyDef);
        body.setUserData(data);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;

        body.createFixture(fixtureDef);
        shape.dispose();

        if (velocity != null)
            body.applyLinearImpulse(velocity.scl(body.getMass()), position, true);
        return body;
    }


    public static Body createCircle(World world, BodyDef.BodyType type, boolean allowSleep,
                                    boolean fixedRotation, Vector2 position, Vector2 velocity,
                                    float radius, UserData data, float density, boolean isSensor,
                                    short groupIndex, short categoryBits, short maskBits) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.allowSleep = allowSleep;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.position.set(position);

        Body body;
        body = world.createBody(bodyDef);
        body.setUserData(data);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;

        fixtureDef.filter.groupIndex = groupIndex;
        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;

        body.createFixture(fixtureDef);
        shape.dispose();

        if (velocity != null)
            body.applyLinearImpulse(velocity.scl(body.getMass()), position, true);
        return body;
    }

    public static void applySquareBlast(final GameWorld gameWorld, final Vector2 blastCenter, float blastRadius, final float blastPower, final boolean destroyAtoms) {
        QueryCallback queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                applyBlastImpulse(fixture.getBody(), blastCenter, blastPower);
                if (destroyAtoms) {
                    if (fixture.getBody().getUserData() instanceof Atom) {
                        gameWorld.freeAtom((Atom) fixture.getBody().getUserData(), true);
                    }
                }
                return true;
            }
        };
        gameWorld.getWorld().QueryAABB(queryCallback, blastCenter.x - blastRadius, blastCenter.y - blastRadius,
                blastCenter.x + blastRadius, blastCenter.y + blastRadius);
    }

    public static void applyBlastImpulse(Body body, Vector2 blastCenter, float blastPower) {
        Vector2 blastDir = body.getWorldCenter().cpy().sub(blastCenter);
        float impulseMag = blastPower / blastDir.len() / blastDir.len();
        body.applyLinearImpulse(blastDir.setLength(impulseMag), body.getWorldCenter(), true);
    }
}
