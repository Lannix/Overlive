package com.lannix.game.controller;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.lannix.game.view.GameWorld;

public class CameraController {

    private GameWorld gameWorld;
    private float minZoom = 0.1f, maxZoom = 5f;
    private OrthographicCamera camera;
    private InputMultiplexer cameraMultiplexer;

    public CameraController(GameWorld gameWorld, OrthographicCamera camera) {
        this.gameWorld = gameWorld;
        this.camera = camera;

        cameraMultiplexer = new InputMultiplexer();
        cameraMultiplexer.addProcessor(getGestureDetector());
        cameraMultiplexer.addProcessor(getInputProcessor());
    }

    public void act(Vector2 target) {
        camera.position.set(target.x, target.y ,0f);
    }

    /************************GestureListener****************************/

    private GestureDetector getGestureDetector() {
        return new GestureDetector(new GestureDetector.GestureAdapter() {
            private float zoom;

            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                zoom = camera.zoom;
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                if (gameWorld.isStepWorld()) {
                    camera.zoom = zoom * initialDistance / distance;

                    if (camera.zoom > maxZoom) camera.zoom = maxZoom;
                    if (camera.zoom < minZoom) camera.zoom = minZoom;
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
            public boolean scrolled(int amount) {
                if (gameWorld.isStepWorld()) {
                    if (amount > 0) camera.zoom *= 1.2;
                    else camera.zoom *= 0.8;

                    if (camera.zoom > maxZoom) camera.zoom = maxZoom;
                    if (camera.zoom < minZoom) camera.zoom = minZoom;
                    return true;
                }
                return false;
            }
        };
    }

    /**********************************************************/


    public float getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public InputMultiplexer getMultiplexer() {
        return cameraMultiplexer;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
}
