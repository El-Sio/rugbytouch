package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 11/12/2016.
 */

public class OptionState extends State {

    private Texture background;
    private Image bgImage;
    private ImageButton playButton;
    private Texture playButtonImg;
    private CheckBox soundToggleCheckbox;
    private CheckBox.CheckBoxStyle soundboxstyle;
    private CheckBox musicToggleCheckbox;

    private Viewport view;

    private Stage optionStage;

    public OptionState(GameStateManager gsm) {
        super(gsm);
        view = new FitViewport(rugbytouch.WIDTH, rugbytouch.HEIGHT, cam);
        background = new Texture("terrain.png");
        Drawable bgdrawable = new TextureRegionDrawable(new TextureRegion(background));
        bgImage = new Image(bgdrawable);

        playButtonImg = new Texture("jouer.png");
        Drawable playdrawable =  new TextureRegionDrawable(new TextureRegion(playButtonImg));
        playButton = new ImageButton(playdrawable);
        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();

        soundboxstyle = new CheckBox.CheckBoxStyle();
        soundboxstyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(new Texture("unchecked_checkbox.png")));
        soundboxstyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(new Texture("checked_checkbox.png")));
        soundboxstyle.font = new BitmapFont();
        soundboxstyle.fontColor = Color.BLACK;

        soundToggleCheckbox = new CheckBox("Sound FX On", soundboxstyle);
        soundToggleCheckbox.setChecked(rugbytouch.rugbysave.getBoolean("FxOn"));

        musicToggleCheckbox = new CheckBox("Music On", soundboxstyle);
        musicToggleCheckbox.setChecked(rugbytouch.rugbysave.getBoolean("MusicOn"));

        optionStage = new Stage(view);
        optionStage.addActor(bgImage);
        optionStage.addActor(soundToggleCheckbox);
        optionStage.addActor(musicToggleCheckbox);
        optionStage.addActor(playButton);
        Gdx.input.setInputProcessor(optionStage);

        soundToggleCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rugbytouch.rugbysave.putBoolean("FxOn", soundToggleCheckbox.isChecked());
                rugbytouch.rugbysave.flush();
            }
        });

        musicToggleCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rugbytouch.rugbysave.putBoolean("MusicOn", musicToggleCheckbox.isChecked());
                rugbytouch.rugbysave.flush();
            }
        });


    }


    @Override
    protected void handleInput() {
        if(playButton.isPressed()) {
            gsm.set(new MenuState(gsm));
            rugbytouch.rugbysave.flush();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        if(rugbytouch.music.isPlaying() && !rugbytouch.rugbysave.getBoolean("MusicOn")) {rugbytouch.music.stop();}
        if(!rugbytouch.music.isPlaying() && rugbytouch.rugbysave.getBoolean("MusicOn")) {rugbytouch.music.play();}
        optionStage.act();

    }

    @Override
    public void render(SpriteBatch sb) {

        musicToggleCheckbox.setPosition(cam.position.x - musicToggleCheckbox.getWidth()/2, cam.position.y);
        soundToggleCheckbox.setPosition(cam.position.x - soundToggleCheckbox.getWidth()/2, cam.position.y - musicToggleCheckbox.getHeight() - 10);
        playButton.setPosition(cam.position.x - playButton.getWidth()/2, cam.position.y - 200);
        optionStage.draw();
    }

    @Override
    public void dispose() {

        optionStage.dispose();
        background.dispose();
        rugbytouch.rugbysave.flush();

    }
}
