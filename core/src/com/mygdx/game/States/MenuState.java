package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 11/12/2016.
 */

public class MenuState extends State{

    private Texture background;
    private Image bgImage;
    private ImageButton playButton;
    private ImageButton selectTeamButton;
    private ImageButton optionButton;
    private Texture playButtonImg;
    private Texture selectTeamButtonImg;
    private Texture optionButtonImg;

    private Viewport view;

    private Stage menuStage;



    public MenuState(final GameStateManager gsm) {
        super(gsm);
        view = new FitViewport(rugbytouch.WIDTH, rugbytouch.HEIGHT, cam);
        background = new Texture("terrain.png");
        Drawable bgdrawable = new TextureRegionDrawable(new TextureRegion(background));
        bgImage = new Image(bgdrawable);

        playButtonImg = new Texture("jouer.png");
        Drawable playdrawable =  new TextureRegionDrawable(new TextureRegion(playButtonImg));
        playButton = new ImageButton(playdrawable);
        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();

        optionButtonImg = new Texture("Option.png");
        Drawable opdrawable = new TextureRegionDrawable(new TextureRegion(optionButtonImg));
        optionButton = new ImageButton(opdrawable);

        selectTeamButtonImg = new Texture("Equipe.png");
        Drawable eqdrawable = new TextureRegionDrawable(new TextureRegion(selectTeamButtonImg));
        selectTeamButton = new ImageButton(eqdrawable);

        menuStage = new Stage(view);
        menuStage.addActor(bgImage);
        menuStage.addActor(playButton);
        menuStage.addActor(selectTeamButton);
        menuStage.addActor(optionButton);
        Gdx.input.setInputProcessor(menuStage);


        selectTeamButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Select Team button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Select Team button released");
                gsm.set(new TeamSelectState(gsm));
            }
        });

        optionButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Options button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Options button released");
                //gsm.set(new OptionState(gsm));
            }
        });

        playButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("play button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("play button released");
                gsm.set(new PlayState(gsm));
            }
        });
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
        menuStage.act(dt);
    }

    @Override
    public void render(SpriteBatch sb) {
        playButton.setPosition(cam.position.x - playButtonImg.getWidth() /2, cam.position.y);
        optionButton.setPosition(cam.position.x - optionButton.getWidth() /2, cam.position.y - playButton.getHeight()*2);
        selectTeamButton.setPosition(cam.position.x - selectTeamButton.getWidth() /2, cam.position.y + playButton.getHeight()*2);
        menuStage.draw();
    }

    @Override
    public void dispose() {

        background.dispose();
        playButtonImg.dispose();
        optionButtonImg.dispose();
        selectTeamButtonImg.dispose();
        menuStage.dispose();
    }
}
