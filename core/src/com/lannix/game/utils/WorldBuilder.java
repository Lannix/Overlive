package com.lannix.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.lannix.game.model.MoleHole;
import com.lannix.game.model.Player;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.ModelUtils.ATOM_RADIUS;
import static com.lannix.game.utils.ModelUtils.MOLE_HOLE_RADIUS;

public class WorldBuilder {

    private WorldData worldData;
    private GameWorld gameWorld;
    private GameData.Chapter.Level levelData;

    public WorldBuilder(GameData.Chapter.Level levelData, GameWorld gameWorld) {
        this.levelData = levelData;
        this.gameWorld = gameWorld;
    }

    public void createGameWorld() {
        Json json = new Json();
        json.addClassTag("basicWorldData", WorldData.class);
        json.addClassTag("vector2", Vector2.class);
        json.addClassTag("playerData", PlayerData.class);
        json.addClassTag("moleHoleData", MoleHoleData.class);
        json.addClassTag("processesData", ProcessesData.class);
        json.addClassTag("randomGenerationData", ProcessesData.RandomGenerationData.class);

        JsonValue value = new JsonReader().parse(Gdx.files.internal(levelData.dataPath)).getChild("gameData");
        worldData = json.readValue(WorldData.class, value);

        //processes
        if (worldData.processesData != null) {
            worldData.processesData.initProcesses(gameWorld);
        }
    }

    public Player getPlayer() {
        PlayerData player = worldData.player;
        return new Player(gameWorld, gameWorld.createAtom(player.position, player.velocity, ATOM_RADIUS));
    }

    public MoleHole getMoleHole() {
        MoleHoleData moleHole = worldData.moleHole;
        return new MoleHole(gameWorld, moleHole.position, MOLE_HOLE_RADIUS, gameWorld.getMoleHoleTextureRegion());
    }

    public static class WorldData {
        public PlayerData player;
        public MoleHoleData moleHole;
        public ProcessesData processesData;
    }

    public static class ProcessesData {
        public String[] processes;
        public RandomGenerationData randomGenerationData;

        public void initProcesses(GameWorld gameWorld) {
            for (int i = 0; i < processes.length; i++) {
                gameWorld.addProcess(createProcess(processes[i], gameWorld));
            }
        }

        public Runnable createProcess(String process, final GameWorld gameWorld) {
            if (process.equals("random_generation")) {
                return new Runnable() {
                    private RandomGenerationData dat = randomGenerationData;
                    private long time = System.currentTimeMillis() / 1000;

                    @Override
                    public void run() {
                        if (System.currentTimeMillis() / 1000 - time >= dat.timeStep) {
                            time = System.currentTimeMillis() / 1000;
                            gameWorld.createRandomAtom(dat.atom, dat.anti, dat.antiSt, dat.rad, dat.event);
                        }
                    }
                };
            }
            return null;
        }

        public static class RandomGenerationData {
            public int atom = 0;
            public int anti = 0;
            public int antiSt = 0;
            public int rad = 0;
            public int event = 0;
            public int timeStep = 1;
        }
    }

    public static class PlayerData {
        public Vector2 position;
        public Vector2 velocity;
    }

    public static class MoleHoleData {
        public Vector2 position;
    }
}
