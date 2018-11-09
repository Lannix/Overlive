package com.lannix.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.lannix.game.controller.CameraController;
import com.lannix.game.model.Antimatter;
import com.lannix.game.model.AntimatterStatic;
import com.lannix.game.model.Atom;
import com.lannix.game.model.AtomEvent;
import com.lannix.game.model.DynamicBackground;
import com.lannix.game.model.MoleHole;
import com.lannix.game.model.Player;
import com.lannix.game.model.Radiation;
import com.lannix.game.utils.GameData;
import com.lannix.game.utils.LevelBuilder;
import com.lannix.game.utils.WorldBuilder;
import com.lannix.game.utils.basic.Background;
import com.lannix.game.utils.basic.GameEvent;
import com.lannix.game.utils.basic.NotGoodAtom;
import com.lannix.game.utils.box2d.UserData;
import com.lannix.game.utils.events.CircleOfDeath;
import com.lannix.game.utils.events.ExplosionOfDeath;
import com.lannix.game.utils.events.MinefieldEvent;
import com.lannix.view.basic.Loader;

import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.lannix.game.utils.GameConstants.BOX2D_HEIGHT;
import static com.lannix.game.utils.GameConstants.BOX2D_WIDTH;
import static com.lannix.game.utils.GameConstants.ONE_STAR;
import static com.lannix.game.utils.GameConstants.POSITION_ITERATIONS;
import static com.lannix.game.utils.GameConstants.THREE_STARS;
import static com.lannix.game.utils.GameConstants.TIME_STEP;
import static com.lannix.game.utils.GameConstants.TWO_STARS;
import static com.lannix.game.utils.GameConstants.VELOCITY_ITERATIONS;
import static com.lannix.game.utils.GameConstants.getBox2dHeight;
import static com.lannix.game.utils.GameConstants.getBox2dWidth;
import static com.lannix.game.utils.GameConstants.initBox2dViewport;
import static com.lannix.game.utils.GameConstants.updateViewport;
import static com.lannix.game.utils.ModelUtils.ANTIMATTER_RADIUS;
import static com.lannix.game.utils.ModelUtils.ATOM_EVENT_RADIUS;
import static com.lannix.game.utils.ModelUtils.ATOM_RADIUS;
import static com.lannix.game.utils.ModelUtils.DAMPING_RATIO;
import static com.lannix.game.utils.ModelUtils.FREQUENCY_HZ;
import static com.lannix.game.utils.ModelUtils.JOINT_LENGTH;
import static com.lannix.game.utils.ModelUtils.MAX_RAND_RADIUS;
import static com.lannix.game.utils.ModelUtils.MIN_RAND_RADIUS;
import static com.lannix.game.utils.ModelUtils.RADIATION_RADIUS;
import static com.lannix.utils.Settings.PLAY_MUSIC;

public class GameWorld extends ScreenAdapter implements Loader {

    //Builder world fields
    private GameData.Chapter.Level levelData;
    private WorldBuilder worldBuilder;

    //View fields
    private InputMultiplexer multiplexer;
    private Batch batch;
    private OrthographicCamera camera;
    private CameraController cameraController;
    private Box2DDebugRenderer renderer;
    private TextureAtlas gameAtlas;
    private Background background;

    //World fields
    private World world;
    private float accumulator = 0;
    private boolean stepWorld = true;
    private ArrayList<Runnable> runs;
    private ArrayList<Runnable> processes;

    //Game Objects fields
    private GameLevel gameLevel;
    private ArrayMap<String, Sound> sounds;
    public static Pools pools;
    private Player player;
    private MoleHole moleHole;

    private long gameTime;

    public GameWorld(Batch batch, GameData.Chapter.Level levelData, GameLevel gameLevel) {
        this.batch = batch;
        this.levelData = levelData;
        this.gameLevel = gameLevel;
    }

    @Override
    public void loadAssetManagerData(AssetManager manager) {
        manager.load("view/game/atoms.atlas", TextureAtlas.class);

        //Load Sounds
        manager.load("sounds/game/absorbing.wav", Sound.class);
        manager.load("sounds/game/annihilation.wav", Sound.class);
        manager.load("sounds/game/big-blast.wav", Sound.class);
        manager.load("sounds/game/crash.wav", Sound.class);
        manager.load("sounds/game/event.wav", Sound.class);
        manager.load("sounds/game/radiation.wav", Sound.class);
    }

    @Override
    public void loadNewThreadData(AssetManager manager) {
        worldBuilder = new WorldBuilder(levelData, this);
    }

    @Override
    public void onDataLoaded(AssetManager manager) {
        gameAtlas = manager.get("view/game/atoms.atlas", TextureAtlas.class);

        sounds = new ArrayMap<String, Sound>();
        sounds.put("absorbing", manager.get("sounds/game/absorbing.wav", Sound.class));
        sounds.put("annihilation", manager.get("sounds/game/annihilation.wav", Sound.class));
        sounds.put("big-blast", manager.get("sounds/game/big-blast.wav", Sound.class));
        sounds.put("crash", manager.get("sounds/game/crash.wav", Sound.class));
        sounds.put("event", manager.get("sounds/game/event.wav", Sound.class));
        sounds.put("radiation", manager.get("sounds/game/radiation.wav", Sound.class));
    }

    @Override
    public void show() {
        //Runnables
        runs = new ArrayList<Runnable>();
        processes = new ArrayList<Runnable>();

        //Box2dRenderer
        renderer = new Box2DDebugRenderer();
        renderer.setDrawBodies(false);

        //Camera
        initBox2dViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, BOX2D_WIDTH, BOX2D_HEIGHT);

        //Background
        background = new DynamicBackground(gameAtlas.findRegion("background"));
        cameraController = new CameraController(this, camera);
        cameraController.setMinZoom(0.05f);
        cameraController.setMaxZoom(8f);

        //Pools
        pools.set(Atom.class, new Pool<Atom>() {
            @Override
            protected Atom newObject() {
                return new Atom();
            }
        });
        pools.set(Antimatter.class, new Pool<Antimatter>() {
            @Override
            protected Antimatter newObject() {
                return new Antimatter();
            }
        });
        pools.set(Radiation.class, new Pool<Radiation>() {
            @Override
            protected Radiation newObject() {
                return new Radiation();
            }
        });
        pools.set(AtomEvent.class, new Pool<AtomEvent>() {
            @Override
            protected AtomEvent newObject() {
                return new AtomEvent();
            }
        });

        //World
        world = new World(new Vector2(0f, 0f), true);
        world.setContactListener(new MyContactListener());

        //Game Objects
        worldBuilder.createGameWorld();
        player = worldBuilder.getPlayer();
        moleHole = worldBuilder.getMoleHole();
        worldBuilder = null;

        //Multiplexer
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(player.getMultiplexer());
        multiplexer.addProcessor(cameraController.getMultiplexer());

        gameTime = System.currentTimeMillis() / 1000;

        updateRuns();
    }

    @Override
    public void render(float delta) {
        act(delta);
        draw();
    }

    public void act(float delta) {
        updateRuns();
        player.act();
        moleHole.act(delta);
        updateCamera();
        actWorld(delta);
        actBodies(delta);
    }

    public void updateRuns() {
        for (int i = 0; i < runs.size(); i++) {
            runs.get(i).run();
        }
        runs.clear();

        for (Runnable runnable : processes) {
            runnable.run();
        }
    }

    public void updateCamera() {
        updateViewport(camera.zoom);
        camera.viewportWidth = BOX2D_WIDTH;
        camera.viewportHeight = BOX2D_HEIGHT;
        cameraController.act(getPlayerPosition());
        camera.update();
    }

    public void actWorld(float delta) {
        if (stepWorld) {
            float frameTime = Math.min(delta, 0.25f);
            accumulator += frameTime;
            while (accumulator >= TIME_STEP) {
                world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
                accumulator -= TIME_STEP;
            }
        }
    }

    public void actBodies(float delta) {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (int i = 0; i < bodies.size; i++) {
            if (bodies.get(i).isActive() && bodies.get(i).getUserData() instanceof UserData) {
                ((UserData) bodies.get(i).getUserData()).act(delta);
            }
        }
    }

    public void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        background.render(batch, camera.position, getBox2dWidth(), getBox2dHeight());
        drawBodies();
        batch.end();

        renderer.render(world, camera.combined);
    }

    public void drawBodies() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (int i = 0; i < bodies.size; i++) {
            if (bodies.get(i).isActive() && bodies.get(i).getUserData() instanceof UserData) {
                ((UserData) bodies.get(i).getUserData()).draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        world.dispose();
        renderer.dispose();
    }

    /*************************World****************************/

    public void addRunnable(Runnable runnable) {
        runs.add(runnable);
    }

    public void addProcess(Runnable runnable) {
        processes.add(runnable);
    }

    public void freeAtom(final Atom atom, boolean postExecute) {
        if (postExecute) {
            runs.add(new Runnable() {
                @Override
                public void run() {
                    pools.free(atom);
                }
            });
        } else {
            pools.free(atom);
        }
    }

    public void addEvent(final GameEvent event, boolean postExecute) {
        if (postExecute) {
            runs.add(new Runnable() {
                @Override
                public void run() {
                    event.execute();
                }
            });
        } else {
            event.execute();
        }
    }

    public Atom createRandomAtom(int atom, int anti, int antiSt, int rad, int event) {
        Atom newAtom;
        int rand = random(1, atom + anti + antiSt + rad + event);
        int max = 0;
        max += atom;
        if (rand <= max) {
            newAtom = createAtom(getPlayerPosition(), ATOM_RADIUS, MIN_RAND_RADIUS, MAX_RAND_RADIUS);
            return newAtom;
        }
        max += anti;
        if (rand <= max) {
            newAtom = createAntimatterDyn(getPlayerPosition(), ANTIMATTER_RADIUS, MIN_RAND_RADIUS, MAX_RAND_RADIUS);
            return newAtom;
        }
        max += antiSt;
        if (rand <= max) {
            newAtom = createAntimatterSt(getPlayerPosition(), ANTIMATTER_RADIUS, MIN_RAND_RADIUS, MAX_RAND_RADIUS);
            return newAtom;
        }
        max += rad;
        if (rand <= max) {
            newAtom = createRadiation(getPlayerPosition(), RADIATION_RADIUS, MIN_RAND_RADIUS, MAX_RAND_RADIUS);
            return newAtom;
        }
        max += event;
        if (rand <= max) {
            newAtom = createAtomEvent(getPlayerPosition(), ATOM_EVENT_RADIUS, MIN_RAND_RADIUS, MAX_RAND_RADIUS);
            return newAtom;
        }
        return null;
    }

    public Atom createRandomAtom(int atom, int anti, int antiSt, int rad, int event, Vector2 pos, Vector2 vel) {
        Atom newAtom;
        int rand = random(1, atom + anti + antiSt + rad + event);
        int max = 0;
        max += atom;
        if (rand <= max) {
            newAtom = createAtom(pos, vel, ATOM_RADIUS);
            return newAtom;
        }
        max += anti;
        if (rand <= max) {
            newAtom = createAntimatterDyn(pos, vel, ANTIMATTER_RADIUS);
            return newAtom;
        }
        max += antiSt;
        if (rand <= max) {
            newAtom = createAntimatterSt(pos, vel, ANTIMATTER_RADIUS);
            return newAtom;
        }
        max += rad;
        if (rand <= max) {
            newAtom = createRadiation(pos, vel, RADIATION_RADIUS);
            return newAtom;
        }
        max += event;
        if (rand <= max) {
            newAtom = createAtomEvent(pos, vel, ATOM_EVENT_RADIUS);
            return newAtom;
        }
        return null;
    }

    public Atom createAtom(Vector2 pos, float radius, float minRad, float maxRad) {
        Atom atom = pools.obtain(Atom.class);
        atom.init(GameWorld.this, radius, getRandomAtomTextureRegion(), pos, minRad, maxRad);
        return atom;
    }

    public Atom createAtom(final Vector2 pos, final Vector2 vel, final float radius) {
        Atom atom = pools.obtain(Atom.class);
        atom.init(GameWorld.this, pos, vel, radius, getRandomAtomTextureRegion());
        return atom;
    }

    public Atom createAntimatterDyn(final Vector2 pos, final float radius, final float minRad, final float maxRad) {
        Atom atom = pools.obtain(Antimatter.class);
        atom.init(GameWorld.this, radius, getRandomAntimatterTextureRegion(), pos, minRad, maxRad);
        return atom;
    }

    public Atom createAntimatterDyn(final Vector2 pos, final Vector2 vel, final float radius) {
        Atom atom = pools.obtain(Antimatter.class);
        atom.init(GameWorld.this, pos, vel, radius, getRandomAntimatterTextureRegion());
        return atom;
    }

    public Atom createAntimatterSt(final Vector2 pos, final float radius, final float minRad, final float maxRad) {
        Atom atom = pools.obtain(AntimatterStatic.class);
        atom.init(GameWorld.this, radius, getRandomAntimatterTextureRegion(), pos, minRad, maxRad);
        return atom;
    }

    public Atom createAntimatterSt(final Vector2 pos, final Vector2 vel, final float radius) {
        Atom atom = pools.obtain(AntimatterStatic.class);
        atom.init(GameWorld.this, pos, vel, radius, getRandomAntimatterTextureRegion());
        return atom;
    }

    public Atom createRadiation(final Vector2 pos, final float radius, final float minRad, final float maxRad) {
        Atom atom = pools.obtain(Radiation.class);
        atom.init(GameWorld.this, radius, getRandomRadiationTextureRegion(), pos, minRad, maxRad);
        return atom;
    }

    public Atom createRadiation(final Vector2 pos, final Vector2 vel, final float radius) {
        Atom atom = pools.obtain(Radiation.class);
        atom.init(GameWorld.this, pos, vel, radius, getRandomRadiationTextureRegion());
        return atom;
    }

    public Atom createAtomEvent(final Vector2 pos, final float radius, final float minRad, final float maxRad) {
        Atom atom = pools.obtain(AtomEvent.class);
        atom.init(GameWorld.this, radius, getAtomEventTextureRegion(), pos, minRad, maxRad);
        return atom;
    }

    public Atom createAtomEvent(final Vector2 pos, final Vector2 vel, final float radius) {
        Atom atom = pools.obtain(AtomEvent.class);
        atom.init(GameWorld.this, pos, vel, radius, getAtomEventTextureRegion());
        return atom;
    }

    public GameEvent createRandomEvent(Vector2 eventPos) {
        GameEvent gameEvent;
        switch (random(2)) {
            case 0:
                gameEvent = new MinefieldEvent(this);
                break;
            case 1:
                gameEvent = new ExplosionOfDeath(this, eventPos);
                break;
            case 2:
                gameEvent = new CircleOfDeath(this);
                break;
            default:
                gameEvent = new MinefieldEvent(this);
        }
        return gameEvent;
    }

    /*
    public void createWall(String type, Vector2 pos, Vector2 dir) {

    }
    */

    private class MyContactListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();
            Object bodyAData = bodyA.getUserData();
            Object bodyBData = bodyB.getUserData();

            if (bodyAData instanceof MoleHole && bodyBData instanceof Atom) {
                if (!player.getBodies().contains(bodyB, true)) {
                    if (PLAY_MUSIC) playSound("absorbing");
                    freeAtom((Atom) bodyBData, true);
                } else {
                    player.pushOutPoint(moleHole.getPosition());
                    playerInMoleHole();
                }
            } else if (bodyBData instanceof MoleHole && bodyAData instanceof Atom) {
                if (!player.getBodies().contains(bodyA, true)) {
                    if (PLAY_MUSIC) playSound("absorbing");
                    freeAtom((Atom) bodyAData, true);
                } else {
                    player.pushOutPoint(moleHole.getPosition());
                    playerInMoleHole();
                }
            }
        }

        @Override
        public void endContact(Contact contact) {
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
            Body bodyA = contact.getFixtureA().getBody();
            Body bodyB = contact.getFixtureB().getBody();
            Object bodyAData = bodyA.getUserData();
            Object bodyBData = bodyB.getUserData();

            if (bodyAData instanceof Atom && bodyBData instanceof Atom) {
                if (bodyAData instanceof Antimatter || bodyBData instanceof Antimatter) {
                    if (PLAY_MUSIC) playSound("annihilation");
                    freeAtom((Atom) bodyAData, true);
                    freeAtom((Atom) bodyBData, true);
                } else if (bodyAData instanceof Radiation || bodyBData instanceof Radiation) {
                    if (PLAY_MUSIC) playSound("radiation");
                    if (bodyAData instanceof Radiation)
                        ((Radiation) bodyAData).prune();
                    else freeAtom((Atom) bodyAData, true);
                    if (bodyBData instanceof Radiation)
                        ((Radiation) bodyBData).prune();
                    else freeAtom((Atom) bodyBData, true);
                } else if (bodyAData instanceof AtomEvent || bodyBData instanceof AtomEvent) {
                    if (PLAY_MUSIC) playSound("event");
                    boolean executed = false;
                    if (bodyAData instanceof AtomEvent) {
                        if (!executed) {
                            addEvent(createRandomEvent(((AtomEvent) bodyAData).getBody().getPosition()), true);
                            executed = true;
                        }
                        freeAtom((Atom) bodyAData, true);
                    }
                    if (bodyBData instanceof AtomEvent) {
                        if (!executed) {
                            addEvent(createRandomEvent(((AtomEvent) bodyBData).getBody().getPosition()), true);
                            executed = true;
                        }
                        freeAtom((Atom) bodyBData, true);
                    }
                } else if (!(bodyAData instanceof NotGoodAtom && bodyBData instanceof NotGoodAtom)) {
                    final DistanceJointDef jointDef = new DistanceJointDef();
                    jointDef.bodyA = bodyA;
                    jointDef.bodyB = bodyB;
                    jointDef.length = JOINT_LENGTH;
                    jointDef.frequencyHz = FREQUENCY_HZ;
                    jointDef.dampingRatio = DAMPING_RATIO;

                    addRunnable(new Runnable() {
                        @Override
                        public void run() {
                            world.createJoint(jointDef);
                        }
                    });
                }
            }
        }
    }


    /*************************View****************************/

    public TextureRegion getRandomAtomTextureRegion() {
        return gameAtlas.findRegion("atom", random(1, 4));
    }

    public TextureRegion getRandomAntimatterTextureRegion() {
        return gameAtlas.findRegion("antimatter", random(1, 3));
    }

    public TextureRegion getRandomRadiationTextureRegion() {
        return gameAtlas.findRegion("radiation", random(1, 2));
    }

    public TextureRegion getAtomEventTextureRegion() {
        return gameAtlas.findRegion("atom-event");
    }

    public TextureRegion getMoleHoleTextureRegion() {
        return gameAtlas.findRegion("mole-hole");
    }

    public void playSound(String sound) {
        sounds.get(sound).play();
    }


    /**************************Game**********************************/

    public float getTargetDirAngleHor() {
        Vector2 dir = getTarget().sub(player.getPosition());
        return dir.angle();
    }

    public Vector2 getTarget() {
        return moleHole.getPosition();
    }

    public Vector2 getPlayerPosition() {
        return player.getPosition();
    }

    public float getPlayerMass() {
        return player.getMass();
    }

    public void playerInMoleHole() {
        gameOver(true);
    }

    public void gameOver(final boolean good) {
        addRunnable(new Runnable() {
            @Override
            public void run() {
                Json json = new Json();
                JsonValue value = new JsonReader().parse(Gdx.files.internal(levelData.dataPath));
                json.addClassTag("game_over", LevelBuilder.GameOver.class);
                LevelBuilder.GameOver gameOver = json.readValue("game_over", LevelBuilder.GameOver.class, value);
                int passValue = gameOver.gameOverValue;

                int points = 0;
                if (good) {
                    float score = 0;
                    if (levelData.type.equals("more_mass")) {
                        score = player.getMass();
                    } else if (levelData.type.equals("survival")) {
                        score = getCurrentGameTimeSec();
                    } else if (levelData.type.equals("labyrinth")) {
                        score = getCurrentGameTimeSec();
                    } else if (levelData.type.equals("mission")) {
                        score = getCurrentGameTimeSec();
                    }

                    if (score >= passValue * THREE_STARS) {
                        points = 3;
                    } else if (score >= passValue * TWO_STARS) {
                        points = 2;
                    } else if (score >= passValue * ONE_STAR) {
                        points = 1;
                    }
                }

                gameLevel.gameOver(levelData, points, levelData.maxPoints);
            }
        });
    }

    public long getCurrentGameTimeSec() {
        return System.currentTimeMillis() / 1000 - gameTime;
    }

    /************************Other******************************/

    public boolean isStepWorld() {
        return stepWorld;
    }

    public void setStepWorld(boolean stepWorld) {
        this.stepWorld = stepWorld;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public World getWorld() {
        return world;
    }

    public TextureAtlas getGameAtlas() {
        return gameAtlas;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Batch getBatch() {
        return batch;
    }
}
