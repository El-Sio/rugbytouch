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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 11/12/2016.
 */

public class TeamSelectState extends State {

    private Array<Texture> teamList;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private Texture nextButtonImg;
    private Texture previousButtonImg;
    private Stage teamSelectStage;
    private Viewport view;
    private Image bgImage;
    private Texture background;
    private int current;
    private Texture currentTexture;
    private Image teamSelectedImg;
    private ImageButton selectButton;
    private Texture selectButtonImg;

    public TeamSelectState(GameStateManager gsm) {
        super(gsm);
        teamList = new Array<Texture>();
        for (int i=0; i<=2; i++) {
            teamList.add(new Texture("player["+i+"].png"));
        }

        current = rugbytouch.rugbysave.getInteger("team");
        rugbytouch.rugbysave.flush();

        view = new FitViewport(rugbytouch.WIDTH, rugbytouch.HEIGHT, cam);
        background = new Texture("terrain.png");
        Drawable bgdrawable = new TextureRegionDrawable(new TextureRegion(background));
        bgImage = new Image(bgdrawable);

        nextButtonImg = new Texture("next.png");
        Drawable nextdrawable = new TextureRegionDrawable(new TextureRegion(nextButtonImg));
        previousButtonImg = new Texture("previous.png");
        Drawable prevdrawable = new TextureRegionDrawable(new TextureRegion(previousButtonImg));
        selectButtonImg = new Texture("jouer.png");
        Drawable selectdrawable = new TextureRegionDrawable(new TextureRegion(selectButtonImg));

        currentTexture = teamList.get(current);
        Drawable playerdrawable = new TextureRegionDrawable(new TextureRegion(currentTexture));
        teamSelectedImg = new Image(currentTexture);

        nextButton = new ImageButton(nextdrawable);
        previousButton =  new ImageButton(prevdrawable);
        selectButton = new ImageButton(selectdrawable);
        selectButton.setChecked(false);

        teamSelectStage = new Stage(view);
        teamSelectStage.addActor(bgImage);
        teamSelectStage.addActor(nextButton);
        teamSelectStage.addActor(teamSelectedImg);
        teamSelectStage.addActor(previousButton);
        teamSelectStage.addActor(selectButton);
        Gdx.input.setInputProcessor(teamSelectStage);

        nextButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Next button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Next button released");
                current++;
                if(current>2)
                        current = 0;
            }
        });

        previousButton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("previous button pressed");
                return true;
            }

            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("previous button released");
                current--;
                if(current<0)
                    current = 2;
            }
        });

    }

    @Override
    protected void handleInput() {

        if(selectButton.isPressed()) {
            gsm.set(new MenuState(gsm));
            rugbytouch.rugbysave.putInteger("team", current);
            rugbytouch.rugbysave.flush();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        teamSelectStage.act();
    }

    @Override
    public void render(SpriteBatch sb) {

        teamSelectedImg.setDrawable(new TextureRegionDrawable(new TextureRegion(teamList.get(current))));
        teamSelectedImg.setPosition(cam.position.x - teamSelectedImg.getWidth()/2, cam.position.y - teamSelectedImg.getHeight()/2);
        previousButton.setPosition(cam.position.x - teamSelectedImg.getWidth() - previousButton.getWidth()/2, cam.position.y);
        nextButton.setPosition(cam.position.x + teamSelectedImg.getWidth() - nextButtonImg.getWidth()/2, cam.position.y);
        selectButton.setPosition(cam.position.x - selectButton.getWidth()/2, cam.position.y - teamSelectedImg.getHeight() - 10);
        teamSelectStage.draw();
    }

    @Override
    public void dispose() {

        for (int i=0; i<2; i++) {
            teamList.get(i).dispose();
        }
        nextButtonImg.dispose();
        background.dispose();
        previousButtonImg.dispose();
        selectButtonImg.dispose();
        teamSelectStage.dispose();
        currentTexture.dispose();
    }
}
