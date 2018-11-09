package com.lannix.game.utils.events;

import com.badlogic.gdx.math.Vector2;
import com.lannix.game.utils.basic.GameEvent;
import com.lannix.game.utils.box2d.Box2dBuilder;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.ModelUtils.BIG_BLAST_POWER;
import static com.lannix.game.utils.ModelUtils.BIG_BLAST_RADIUS;
import static com.lannix.game.utils.ModelUtils.DISTRUCTION_BIG_BLAST_RADIUS;

public class ExplosionOfDeath implements GameEvent {

    private GameWorld gameWorld;
    private Vector2 blastPosition;

    public ExplosionOfDeath(GameWorld gameWorld, Vector2 blastPosition) {
        this.gameWorld = gameWorld;
        this.blastPosition = blastPosition;
    }

    @Override
    public void execute() {
        gameWorld.playSound("big-blast");

        Box2dBuilder.applySquareBlast(gameWorld, blastPosition,
                BIG_BLAST_RADIUS, BIG_BLAST_POWER, false);

        Box2dBuilder.applySquareBlast(gameWorld, blastPosition,
                DISTRUCTION_BIG_BLAST_RADIUS, 0f, true);
    }
}
