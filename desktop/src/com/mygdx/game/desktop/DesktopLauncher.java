package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.rugbytouch;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = rugbytouch.WIDTH*2;
		config.height = rugbytouch.HEIGHT*2;
		config.title = rugbytouch.TITLE;
		new LwjglApplication(new rugbytouch(), config);
	}
}
