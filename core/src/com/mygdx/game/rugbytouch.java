package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.States.GameStateManager;
import com.mygdx.game.States.MenuState;

public class rugbytouch extends ApplicationAdapter {

	SpriteBatch batch;

	public static Preferences rugbysave;
	public static final int WIDTH = 480;
	public static final int HEIGHT = 800;
	public static final String TITLE = "Rugby Touch";

	private GameStateManager gsm;

	public static Music music;
	public static int HighScore;
	public static boolean musicOn;
	public static boolean fxOn;
	public static boolean Paused;

	@Override
	public void create () {

		rugbysave = Gdx.app.getPreferences("My Preferences");
		HighScore = rugbysave.getInteger("Highscore");
		rugbysave.flush();
		musicOn = rugbysave.getBoolean("MusicOn");
		rugbysave.flush();
		fxOn = rugbysave.getBoolean("FxOn");
		rugbysave.flush();
		gsm = new GameStateManager();
		batch = new SpriteBatch();
		musicOn = true;
		fxOn = true;
		Paused = false;
		//music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		//music.setLooping(true);
		//music.setVolume(0.1f);
		//if(musicOn) {music.play();};
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	@Override
	public void pause() {
		Paused = true;
		System.out.println("app paused");
	}

	@Override
	public void resume() {
		System.out.println("app resumed");
		render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		rugbysave.flush();
	}
}