package com.lannix.game.utils.events;

import com.badlogic.gdx.math.Vector2;
import com.lannix.game.utils.basic.GameEvent;
import com.lannix.game.view.GameWorld;

import static com.lannix.game.utils.ModelUtils.CIRCLE_DEF_RADIUS;
import static com.lannix.game.utils.ModelUtils.NUMBER_OF_ATOMS;

public class CircleOfDeath implements GameEvent {

    private GameWorld gameWorld;

    public CircleOfDeath(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void execute() {
        Vector2 playerPos = gameWorld.getPlayerPosition();
        Vector2 curDir = new Vector2(1f, 0f).setLength(CIRCLE_DEF_RADIUS);
        float angle = 360f / NUMBER_OF_ATOMS;
        for (int i = 0; i < NUMBER_OF_ATOMS; i++) {
            gameWorld.createRandomAtom(5, 1,1,1,0, playerPos.cpy().add(curDir), new Vector2());
            curDir.rotate(angle);
        }
    }
}
