package com.lannix.game.utils.events;

import com.badlogic.gdx.math.Vector2;
import com.lannix.game.model.AntimatterStatic;
import com.lannix.game.utils.basic.GameEvent;
import com.lannix.game.view.GameWorld;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.lannix.game.utils.ModelUtils.ANTIMATTER_RADIUS;
import static com.lannix.game.utils.ModelUtils.DIST_BETWEEN_ANTIMATTERS;
import static com.lannix.game.utils.ModelUtils.MAX_COLUMNS_ROWS;
import static com.lannix.game.utils.ModelUtils.MAX_RAND_RADIUS;
import static com.lannix.game.utils.ModelUtils.MIN_RAND_RADIUS;

public class MinefieldEvent implements GameEvent {

    private GameWorld gameWorld;

    public MinefieldEvent(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void execute() {
        int rows = random(1, MAX_COLUMNS_ROWS);
        int columns = random(1, MAX_COLUMNS_ROWS);
        Vector2 systemPosition = new Vector2(random(-1f, 1f),
                random(-1f, 1f)).setLength(random(MIN_RAND_RADIUS, MAX_RAND_RADIUS))
                .add(gameWorld.getPlayerPosition())
                .sub(columns / 2f * (ANTIMATTER_RADIUS + DIST_BETWEEN_ANTIMATTERS),
                        rows / 2f * (ANTIMATTER_RADIUS + DIST_BETWEEN_ANTIMATTERS));

        boolean bias = false;
        Vector2 pos = new Vector2();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                AntimatterStatic antimatter = gameWorld.pools.obtain(AntimatterStatic.class);
                pos.set(systemPosition.cpy().add(j * DIST_BETWEEN_ANTIMATTERS, i * DIST_BETWEEN_ANTIMATTERS));
                if (bias) pos.sub(DIST_BETWEEN_ANTIMATTERS / 2f, 0f);
                antimatter.init(gameWorld, pos, null, ANTIMATTER_RADIUS, gameWorld.getRandomAntimatterTextureRegion());
            }
            bias = !bias;
        }
    }
}
