package com.lannix.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lannix.utils.Constants;
import com.lannix.view.Overlive;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = Constants.TITLE;
		config.width = 800;
		config.height = 600;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.vSyncEnabled = true;
		config.addIcon("view/icon/icon_16.png", Files.FileType.Internal);
		config.addIcon("view/icon/icon_32.png", Files.FileType.Internal);
		config.addIcon("view/icon/icon_128.png", Files.FileType.Internal);

		new LwjglApplication(new Overlive(), config);
	}
}
