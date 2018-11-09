package com.lannix.game.model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.lannix.game.utils.basic.NotGoodAtom;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.ModelUtils.PLAYER_ADDITION_VELOCITY;
import static com.lannix.game.utils.ModelUtils.PLAYER_BOOM_VELOCITY;

public class Player {

    private float mass;
    private Vector2 position;
    private Body mainBody;
    private Array<Body> bodies;
    private GameWorld gameWorld;
    private InputMultiplexer playerMultiplexer;

    public Player(GameWorld gameWorld, Atom atom) {
        this.gameWorld = gameWorld;

        mainBody = atom.getBody();
        mass += mainBody.getMass();

        bodies = new Array<Body>();
        bodies.add(mainBody);

        playerMultiplexer = new InputMultiplexer();
        playerMultiplexer.addProcessor(getInputProcessor());
        playerMultiplexer.addProcessor(getGestureListener());

        position = mainBody.getPosition().cpy();
    }

    /************************GestureListener****************************/

    private GestureDetector getGestureListener() {
        return new GestureDetector(new GestureDetector.GestureAdapter() {
            private float delta;
            private Vector3 tap = new Vector3();
            private Vector2 vel = new Vector2();

            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (gameWorld.isStepWorld()) {
                    tap = gameWorld.getCamera().unproject(tap.set(x, y, 0f));
                    Body body;
                    for (int i = 0; i < bodies.size; i++) {
                        body = bodies.get(i);
                        vel.set(tap.x, tap.y).sub(body.getPosition()).setLength(PLAYER_ADDITION_VELOCITY * count);
                        body.setLinearVelocity(body.getLinearVelocity().add(vel));
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                if (gameWorld.isStepWorld()) {
                    tap = gameWorld.getCamera().unproject(tap.set(x, y, 0f));
                    Body body;
                    for (int i = 0; i < bodies.size; i++) {
                        body = bodies.get(i);
                        tap = gameWorld.getCamera().unproject(tap.set(x, y, 0f));
                        body.setLinearVelocity(body.getLinearVelocity().add(vel.set(tap.x, tap.y)
                                .sub(body.getPosition()).setLength(PLAYER_ADDITION_VELOCITY)));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**********************************************************/


    /**********************InputProcessor*********************/

    private InputProcessor getInputProcessor() {
        return new InputAdapter() {

            @Override
            public boolean keyUp(int keycode) {
                if (gameWorld.isStepWorld()) {
                    switch (keycode) {
                        case Input.Keys.LEFT:
                            addToLinearVelocity(new Vector2(-PLAYER_ADDITION_VELOCITY, 0f));
                            break;
                        case Input.Keys.RIGHT:
                            addToLinearVelocity(new Vector2(PLAYER_ADDITION_VELOCITY, 0f));
                            break;
                        case Input.Keys.UP:
                            addToLinearVelocity(new Vector2(0f, PLAYER_ADDITION_VELOCITY));
                            break;
                        case Input.Keys.DOWN:
                            addToLinearVelocity(new Vector2(0f, -PLAYER_ADDITION_VELOCITY));
                            break;
                    }
                    return true;
                }
                return false;
            }
        };
    }

    /**********************************************************/


    private void addToLinearVelocity(Vector2 vel) {
        Body body;
        for (int i = 0; i < bodies.size; i++) {
            body = bodies.get(i);
            body.setLinearVelocity(body.getLinearVelocity().add(vel));
        }
    }

    public void act() {
        if (!mainBody.isActive() || mainBody.getUserData() instanceof NotGoodAtom) {
            boolean mainBodyIsSuit = false;
            for (int i = 0; i < bodies.size; i++) {
                if (bodies.get(i).isActive() || !(bodies.get(i).getUserData() instanceof NotGoodAtom)) {
                    mainBody = bodies.get(i);
                    mainBodyIsSuit = true;
                }
            }
            gameWorld.gameOver(false);
        }

        bodies.clear();
        mass = mainBody.getMass();
        updateBodies(mainBody);
    }

    private void updateBodies(Body parentBody) {
        bodies.add(parentBody);
        Body childBody;
        for (int i = 0; i < parentBody.getJointList().size; i++) {
            childBody = parentBody.getJointList().get(i).other;

            if (!bodies.contains(childBody, true)) {
                mass += childBody.getMass();
                updateBodies(childBody);
            }
        }
    }

    public void pushOutPoint(Vector2 point) {
        Body body;
        for (int i = 0; i < bodies.size; i++) {
            body = bodies.get(i);
            body.setLinearVelocity(body.getPosition().cpy().sub(point)
                    .setLength(PLAYER_BOOM_VELOCITY));
        }
        bodies.clear();
        bodies.add(mainBody);
    }

    public void pushInPoint(Vector2 point) {
        Body body;
        for (int i = 0; i < bodies.size; i++) {
            body = bodies.get(i);
            body.setLinearVelocity(point.cpy().sub(body.getPosition())
                    .setLength(PLAYER_BOOM_VELOCITY));
        }
        bodies.clear();
        bodies.add(mainBody);
    }

    public void destroyJoints() {
        for (int i = 0; i < bodies.size; i++) {
            for (int j = 0; j < bodies.get(i).getJointList().size; j++) {
                gameWorld.getWorld().destroyJoint(bodies.get(i).getJointList().get(j).joint);
            }
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Body getMainBody() {
        return mainBody;
    }

    public Array<Body> getBodies() {
        return bodies;
    }

    public InputMultiplexer getMultiplexer() {
        return playerMultiplexer;
    }

    public float getMass() {
        return mass;
    }

    public Vector2 getPosition() {
        position.set(0f, 0f);
        if (bodies.size != 0) {
            float mass = 0;
            for (int i = 0; i < bodies.size; i++) {
                if (bodies.get(i).isActive()) {
                    position.x += bodies.get(i).getMass() * bodies.get(i).getPosition().x;
                    position.y += bodies.get(i).getMass() * bodies.get(i).getPosition().y;
                    mass += bodies.get(i).getMass();
                }
            }
            position.scl(1f / mass);
        }
        return position;
    }
}
