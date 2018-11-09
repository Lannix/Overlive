package com.lannix.view;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.lannix.utils.Constants;
import com.lannix.utils.Settings;
import com.lannix.view.main_menu.StartMenu;

import static com.lannix.view.basic.ScreenLoader.swapScreenWithLoader;

public class Overlive extends Game {

	@Override
	public void create() {
		Settings.load();
		Constants.init();
		setWindowedFullScreen();
		swapScreenWithLoader(new StartMenu(this));
	}

	private void setWindowedFullScreen() {
		if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
			Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width,
					Gdx.graphics.getDisplayMode().height);
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(Constants.BACKGROUND_COLOR_RED, Constants.BACKGROUND_COLOR_GREEN,
				Constants.BACKGROUND_COLOR_BLUE, Constants.BACKGROUND_COLOR_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		Constants.update(width, height);
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
		Settings.save();
	}
}
