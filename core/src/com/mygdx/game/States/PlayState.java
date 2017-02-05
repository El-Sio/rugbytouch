package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Sprites.Ball;
import com.mygdx.game.Sprites.EnnemyPlayer;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.rugbytouch;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Random;

/**
 * Created by charl on 11/12/2016.
 *
 * This is the main game screen to handle actual game mechanics
 *
 */

public class PlayState extends State implements GestureDetector.GestureListener {

    private Ball ball;
    private Array<Player> teamA;
    private Array<EnnemyPlayer> teamB;
    private static final int PLAYERCOUNT = 4;
    private Texture background;
    private int LIVES;
    private Random rand;
    private int position;
    private int positionattaque;
    private Array<Texture> lifeArray;

    private Stage gameOver;
    private ImageButton gameOverButton;
    private Texture gameOverButtonImg;
    private Boolean isGameOver;

    private Stage ruckBar;
    private Array<Image> forceArray;
    private Boolean isRuck;
    private int ruckingplayer;
    private Boolean ruckresolved;

    private Sound ruckOverSound;

    public PlayState(GameStateManager gsm,int lives) {

        super(gsm);
        LIVES = lives;

        ruckingplayer = -1;

        ruckOverSound = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
        ruckOverSound.setVolume(0,5f);

        //Random Start Point
        rand = new Random();
        //position = rand.nextInt(PLAYERCOUNT+1);
        positionattaque = rand.nextInt(PLAYERCOUNT+1);

        //never start in front of the gap
        /*
        while(positionattaque==position) {
            positionattaque = rand.nextInt(PLAYERCOUNT+1);
        }
        */

        //Draw amount of remaining lives indicator (ball texture)
        lifeArray = new Array<Texture>(LIVES);
        for(int i = 0; i<=LIVES; i++) {
            lifeArray.add(new Texture("ball.png"));
        }

        //TODO find a way to zoom out of the field and have a bigger bg image
        cam.setToOrtho(false, rugbytouch.WIDTH, rugbytouch.HEIGHT);
        background = new Texture("terrain.png");

        //simple Game Over Splash Screen
        gameOverButtonImg = new Texture("gameover.png");
        gameOverButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(gameOverButtonImg)));
        gameOver = new Stage(new FitViewport(rugbytouch.WIDTH, rugbytouch.HEIGHT));
        gameOver.addActor(gameOverButton);
        gameOverButton.setPosition(cam.position.x - gameOverButtonImg.getWidth()/2, cam.position.y);
        gameOver.act();

        isGameOver = false;

        //During Ruck Phase, displays a "bar" showing the opposing Strength.
        ruckBar = new Stage(new FitViewport(rugbytouch.WIDTH, rugbytouch.HEIGHT));
        forceArray = new Array<Image>(9);
        ruckBar.act();

        isRuck = false;
        ruckresolved = false;

        //create the Teams
        teamA = new Array<Player>(PLAYERCOUNT);
        teamB = new Array<EnnemyPlayer>(PLAYERCOUNT);

        //Fill the teams with players
        //Team B forms a LIne
        //Team A is an arrow with the tip at the random start position

        for(int i=0; i<=PLAYERCOUNT; i++) {

            if(i<=positionattaque) {
                teamA.add(new Player(100 * (i + 1), 300 - (positionattaque - i) * 100));
                teamA.get(i).hasBall = false;

            }
            if(i>positionattaque) {
                teamA.add(new Player(100 * (i + 1), 300 - (i - positionattaque) * 100));
                teamA.get(i).hasBall = false;
            }
            teamB.add(new EnnemyPlayer(100*(i+1), 700, true));
        }

        //Create the Ball in front of the Random starting position
        ball = new Ball(100+ 100*(positionattaque),400);

        //Listen to the User Input (tap and fling supported)
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);
    }

    @Override
    protected void handleInput() {

        //Handle input on the "game over popup"
        if(isGameOver) {
            rugbytouch.Paused = true;
            Gdx.input.setInputProcessor(gameOver);
            if(Gdx.input.justTouched()) {
                rugbytouch.Paused = false;
                gsm.set(new MenuState(gsm));
            }
        }
    }

    @Override
    public void update(float dt) {

        //TODO see if I can put the gesture control in the handleinput method
        handleInput();

        //Game only updates when not paused (after app lost focus)
        if(!rugbytouch.Paused) {

            //Loop through all the players
            for (int i = 0; i <= PLAYERCOUNT; i++) {
                //Move them around
                teamA.get(i).update(dt);
                if(teamA.get(i).isRucking)  {
                    if(teamA.get(i).force < 10) {
                        teamA.get(i).force = teamA.get(i).force - 3 * dt;
                        System.out.println(teamA.get(i).force);
                    }
                    if(teamA.get(i).force<1) {
                        if (rugbytouch.rugbysave.getBoolean("FxOn"))
                            teamA.get(i).plaquedSound.play();
                        teamA.get(i).plaqued = true;
                        LIVES--;
                        //Game is over when life counter reachs 0 and brings you back to menu
                        if (LIVES < 0) {
                            isGameOver = true;
                        }
                        if (LIVES >= 0) {
                            //Game restarts with one life less.
                            gsm.set(new PlayState(gsm, LIVES));
                        }
                    }
                }

                if(!teamA.get(i).hasBall && teamA.get(i).isRucking && teamB.get(i).getMOVEMENT() == 0) {
                    //the ruck is over after the rucking player made a pass
                    for(int j=0; j<=PLAYERCOUNT; j++) {
                        teamA.get(j).setMOVEMENT(100);
                        teamB.get(j).setMOVEMENT(-50);
                    }
                    isRuck = false;
                    teamA.get(i).isRucking = false;
                    teamA.get(i).force = 5;
                    ruckingplayer = -1;
                }

                //Check if a player has scored. It must hold the ball (hasBall = true) and have crossed the line
                //TODO have the scoring line's height different fom the viewport's height on an "absolute" field
                if (ball.getPosition().y > rugbytouch.HEIGHT + 41 && teamA.get(i).hasBall) {
                    if(rugbytouch.rugbysave.getBoolean("FxOn"))
                        teamA.get(i).essaiSound.play();
                    //Scoring gives you an extra life and restarts a game situation
                    if(LIVES<3) {LIVES++;}
                    gsm.set(new PlayState(gsm, LIVES));
                }

                //Move the enemy around
                teamB.get(i).update(dt);

                // Detect collision events with the ball. The result is that current player has the ball (hasBall = true)
                if (teamA.get(i).collide(ball.getBounds())) {
                    teamA.get(i).hasBall = true;

                    //When player i has the ball, player i+1 and i-1 or i+2 and i-2 stop holding it
                    //TODO improve thhis code using array funtions ? of find a way to solve the "sticky ball" that allows for changing the ball older on calling the Pass() method.
                    if(i>1) {
                        teamA.get(i - 1).hasBall = false;
                        teamA.get(i-2).hasBall = false;
                    }
                    if(i<PLAYERCOUNT-1) {
                        teamA.get(i + 1).hasBall = false;
                        teamA.get(i+2).hasBall = false;
                    }

                    //When player holds the ball, it gets "attached" to it and moves with it.
                    ball.setPosition(new Vector3(teamA.get(i).getPosition().x + teamA.get(i).getTexture().getRegionWidth() / 2, teamA.get(i).getPosition().y + teamA.get(i).getTexture().getRegionHeight() / 2, 0));
                    ball.setMOVEMENT(0);
                    ball.setGRAVITY(0);
                    ball.setVelocity(new Vector3(0, 0, 0));
                }

                // Check if ennemy players touches the ball, resulting in a loss of life ahndled by the deadball routine below
                if(teamB.get(i).collide(ball.getBounds()))
                {
//                    ball.dead = true;
                }

                // Handles collision between player and the ennemy facing him
                //TODO imprive with a method to detect collision with any other player to allow for horizontal movement of players.
                if (teamA.get(i).collide(teamB.get(i).getBounds())) {

                    //Check if the collision happened to the player carrying the ball
                    if (teamA.get(i).hasBall) {

                        if(teamA.get(i).isCharging) {
                            teamA.get(i).slowdown();
                            teamA.get(i).isRucking = true;
                            isRuck = true;
                            ruckingplayer = i;
                            for(int j=0; j<=PLAYERCOUNT; j++) {
                                teamA.get(j).setMOVEMENT(0);
                                teamB.get(j).setMOVEMENT(0);
                            }
                        }

                        if (!teamA.get(i).isCharging && !teamA.get(i).isRucking) {
                            //In that case the player gets "tackled" (plaqué in french) and loses a life.
                            if (rugbytouch.rugbysave.getBoolean("FxOn"))
                                teamA.get(i).plaquedSound.play();
                            teamA.get(i).plaqued = true;
                            LIVES--;
                            //Game is over when life counter reachs 0 and brings you back to menu
                            if (LIVES < 0) {
                                isGameOver = true;
                            }
                            if (LIVES >= 0) {
                                //Game restarts with one life less.
                                gsm.set(new PlayState(gsm, LIVES));
                            }
                        }
                    }
                }

                //This section allows players to become "solid" after clearing their opponent in order to be able to receive pass after the first row.
                //TODO this section should be rendered obsolete by a better solution to the sticky ball problem.
                if(!teamA.get(i).hasBall && teamA.get(i).getPosition().y > teamB.get(i).getPosition().y + teamB.get(i).getTexture().getRegionHeight() + 10 && i!=PLAYERCOUNT) {
                    if(i==0 && teamA.get(1).hasBall)
                        teamA.get(i).setBounds(new Rectangle(teamA.get(i).getPosition().x, teamA.get(i).getPosition().y, teamA.get(i).getTexture().getRegionWidth(), teamA.get(i).getTexture().getRegionHeight()));
                    else if(i!=0)
                        teamA.get(i).setBounds(new Rectangle(teamA.get(i).getPosition().x, teamA.get(i).getPosition().y, teamA.get(i).getTexture().getRegionWidth(), teamA.get(i).getTexture().getRegionHeight()));
                }
            }

            //move the ball around if needed, namely when passing the ball.
            ball.update(dt);

            //Center camera on the ball.
            //TODO handle the last movement of the ball after collision

            cam.position.x = ball.getPosition().x;
            cam.position.y = ball.getPosition().y;

            //Handles the case where ball "falls" to the bottom of the screen or is touched by an ennemy player.
            if (ball.dead) {

                LIVES --;
                if(LIVES < 0) {
                    isGameOver = true;
                }
                if(LIVES >=0) {
                    gsm.set(new PlayState(gsm, LIVES));
                }
            }

            //Update the camera rendering
            cam.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        //Draw everithing centered on the ball.
        sb.setProjectionMatrix(cam.combined);
        sb.begin();

        //Draw the field
        //TODO use larger background or "divide the field in smaller textures
        sb.draw(background,0,0);

        //Draw the players
        for(int i = 0; i<=PLAYERCOUNT; i++) {
            sb.draw(teamA.get(i).getTexture(), teamA.get(i).getPosition().x, teamA.get(i).getPosition().y);
            sb.draw(teamB.get(i).getTexture(), teamB.get(i).getPosition().x, teamB.get(i).getPosition().y);
        }

        if(isRuck) {
            ruckBar.clear();
            forceArray.clear();
            for (int j = 0; j < Math.min(Math.round(teamA.get(ruckingplayer).force),10); j++) {
                forceArray.add(new Image(new TextureRegionDrawable(new TextureRegion(new Texture("ball.png")))));
                ruckBar.addActor(forceArray.get(j));
                forceArray.get(j).setPosition(20+42*j, cam.position.y);
            }
            if(teamA.get(ruckingplayer).force>=10) {
                if(!ruckresolved) {
                    ruckresolved = true;
                    position = rand.nextInt(PLAYERCOUNT+1);
                    while (position == ruckingplayer) {
                        position = rand.nextInt(PLAYERCOUNT+1);
                    }
                    if(position!=0) {
                        teamB.set(position, new EnnemyPlayer(100 * (position + 1), Math.round(teamB.get(0).getPosition().y), false));
                    }
                    if (position == 0) {
                        teamB.set(position, new EnnemyPlayer(100 * (position + 1), Math.round(teamB.get(1).getPosition().y), false));
                    }
                        if (rugbytouch.rugbysave.getBoolean("FxOn")) {
                        ruckOverSound.play();
                    }
                }
                Texture tmpTxt = new Texture("useit.png");
                Image useIt = new Image(new TextureRegionDrawable(new TextureRegion(tmpTxt)));
                useIt.setPosition(240 - tmpTxt.getWidth()/2, cam.position.y - 2*tmpTxt.getHeight());
                ruckBar.addActor(useIt);
                }
            ruckBar.draw();
        }

        //Draw Ball
        sb.draw(ball.getTexture(), ball.getPosition().x, ball.getPosition().y);

        //Draw the life counter last so that it is always displayed on top
        for (int i=0; i<=LIVES; i++) {
            sb.draw(lifeArray.get(i), cam.position.x + cam.viewportWidth/2 - 42*i, cam.position.y - cam.viewportHeight/2);
        }
        sb.end();

        if(isGameOver) {
            gameOver.draw();
        }
    }

    @Override
    public void dispose() {

        //Delete all textures and sounds.
        for(int i = 0; i<=PLAYERCOUNT; i++) {
            teamA.get(i).dispose();
            teamB.get(i).dispose();
        }
        ball.dispose();
        background.dispose();

        for(int i = 0; i<=LIVES-1; i++) {
            lifeArray.get(i).dispose();
        }

        gameOver.dispose();
        ruckBar.dispose();
        forceArray.clear();
    }

    //This section handles gestures
    //TODO put those calls in handleinput method

    //Do not implement simple touchdown
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    //Handle TAP to modify the player's vertical movement calling the 'charge' or 'slowdown' methods
    //TODO use those to implement "ruck" states that resolve in loss of ball or ability to go through and pass.
    @Override
    public boolean tap(float x, float y, int count, int button) {

        //Each implemented gesture can take you out of paused state.
        if(rugbytouch.Paused) {
            rugbytouch.Paused = false;
        }
        for (int i = 0; i <= PLAYERCOUNT; i++) {
            if(teamA.get(i).isRucking) {
                //Tap to oppose the ennemy's strength
                teamA.get(i).force++;
                System.out.println(teamA.get(i).force);
            }
            else if (teamA.get(i).hasBall) {
                if(!teamA.get(i).isCharging) {
                    teamA.get(i).charge();
                }
                else {
                    teamA.get(i).slowdown();
                }
            }
        }
        return false;
        }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    //Handle fling to pass the ball. "rapid" fling allows for a long pass (passe sautée in french) short fling for a simple pass. Pass can go left or right depending on the direction of the fling.
    //TODO : handle vertical fings to allow for "kicks over defense"

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        if (rugbytouch.Paused) {
            rugbytouch.Paused = false;
        }

        if(Math.abs(velocityX)>Math.abs(velocityY)) {
            for (int i = 0; i <= PLAYERCOUNT; i++) {
                    //ONly the ball carrier can pass it. A rucking player can only pass after winning the ruck.
                if (teamA.get(i).hasBall && (teamA.get(i).force>10 || !teamA.get(i).isRucking)) {
                    //Before starting the ball movement, we must end collision events with the player by setting it's bounds temporarly to an empty rectangle.
                    //TODO find a better way to pass the ball and keep player "solid" probably using box2d
                    isRuck = false;
                    forceArray.clear();
                    ruckresolved = false;
                    teamA.get(i).setBounds(new Rectangle(0, 0, 0, 0));
                    if (!teamA.get(i).plaqued) {
                        //Simple pass to the right
                        if(velocityX>0 && velocityX<5000) {
                            //Player on the extremities must 'loose' ball as soon as they pass it and not wait for adjascent player to reveive it.
                            //TODO this should be simplified by the better solution to the sticky ball problem
                            if(i==PLAYERCOUNT)
                                teamA.get(i).hasBall = false;
                            ball.pass(true, teamA.get(i).isCharging, false);
                            if(i==0){
                                teamA.get(i).hasBall = false;
                                teamA.get(i+1).hasBall = false;
                            }
                        }
                        //Long pass to the right
                        //TODO same as previous comment for extremities.
                        else if(velocityX>0 && velocityX>5000) {
                            if(i==PLAYERCOUNT)
                                teamA.get(i).hasBall = false;
                            ball.pass(true, teamA.get(i).isCharging, true);
                            if(i==0){
                                teamA.get(i).hasBall = false;
                                teamA.get(i+1).hasBall = false;
                            }
                        }
                        //Simple pass to the left.
                        else if(velocityX<0 && velocityX>-5000) {
                            if(i==PLAYERCOUNT)
                                teamA.get(i).hasBall = false;
                            ball.pass(false, teamA.get(i).isCharging, false);
                            if(i==0){
                                teamA.get(i).hasBall = false;
                                teamA.get(i+1).hasBall = false;
                            }
                        }
                        //long pass to the left
                        else if(velocityX<0 && velocityX<-5000) {
                            if(i==PLAYERCOUNT)
                                teamA.get(i).hasBall = false;
                            ball.pass(false, teamA.get(i).isCharging, true);
                            if(i==0) {
                                teamA.get(i).hasBall = false;
                                teamA.get(i+1).hasBall = false;
                            }
                        }
                        else {
                            //no horizontal direction = no pass
                            //TODO handle drop kicks here
                        }
                    }
                }
            }
            }
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
