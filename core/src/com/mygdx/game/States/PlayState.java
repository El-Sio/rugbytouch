package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Sprites.Ball;
import com.mygdx.game.Sprites.EnnemyPlayer;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.rugbytouch;

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

    public PlayState(GameStateManager gsm,int lives) {

        super(gsm);
        LIVES = lives;

        //Random Gap and Start Points
        rand = new Random();
        position = rand.nextInt(PLAYERCOUNT+1);
        positionattaque = rand.nextInt(PLAYERCOUNT+1);

        //never start in front of the gap
        while(positionattaque==position) {
            positionattaque = rand.nextInt(PLAYERCOUNT+1);
        }

        //Draw amount of remaining lives indicator (ball texture)
        lifeArray = new Array<Texture>(LIVES);
        for(int i = 0; i<=LIVES; i++) {
            lifeArray.add(new Texture("ball.png"));
        }

        //TODO find a way to zoom out of the field and have a bigger bg image
        cam.setToOrtho(false, rugbytouch.WIDTH, rugbytouch.HEIGHT);
        background = new Texture("terrain.png");

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
            if(i!=position) {
                teamB.add(new EnnemyPlayer(100*(i+1), 700, true));
            }
            if(i==position) {
                teamB.add(new EnnemyPlayer(100*(i+1), 700, false));
            }
        }

        //Create the Ball in front of the Random starting position
        ball = new Ball(100+ 100*(positionattaque),400);

        //Listen to the User Input (tap and fling supported)
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);
    }

    @Override
    protected void handleInput() {

        //Nothing to see here since gesture is handled directly by the instance.
    }

    @Override
    public void update(float dt) {

        //TODO see if I can put the gesture control in the handleinput method
        handleInput();

        //Game only updates when not paused (after app lost focus)
        if(!rugbytouch.Paused) {

            //Loo^p through all the players
            for (int i = 0; i <= PLAYERCOUNT; i++) {
                //Move them around
                teamA.get(i).update(dt);

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
                    ball.dead = true;
                }

                // Handles collision between player and the ennemy facing him
                //TODO imprive with a method to detect collision with any other player to allow for horizontal movement of players.
                if (teamA.get(i).collide(teamB.get(i).getBounds())) {

                    //Check if the collision happened to the player carrying the ball
                    if (teamA.get(i).hasBall) {

                        //In that case the player gets "tackled" (plaqué in french) and loses a life.
                        if (rugbytouch.rugbysave.getBoolean("FxOn"))
                            teamA.get(i).plaquedSound.play();
                        teamA.get(i).plaqued = true;
                        LIVES--;
                        //Game is over when life counter reachs 0 and brings you back to menu
                        if(LIVES == 0) {
                            //TODO : display a "game over message" over the paused game state before going back to menu
                            gsm.set(new MenuState(gsm));
                        }
                        if(LIVES >0) {
                            //Game restarts with one life less.
                            gsm.set(new PlayState(gsm, LIVES));
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
                if(LIVES == 0) {
                    //TODO display a Game Over message before returning to menu
                    gsm.set(new MenuState(gsm));
                }
                if(LIVES >0) {
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

        //Draw Ball
        sb.draw(ball.getTexture(), ball.getPosition().x, ball.getPosition().y);

        //Draw the life counter last so that it is always displayed on top
        for (int i=0; i<=LIVES; i++) {
            sb.draw(lifeArray.get(i), cam.position.x + cam.viewportWidth/2 - 42*i, cam.position.y - cam.viewportHeight/2);
        }

        sb.end();
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
            for (int i = 0; i <= PLAYERCOUNT; i++) {
                if (teamA.get(i).hasBall) {
                    if(!teamA.get(i).isCharging) {
                        teamA.get(i).charge();
                    }
                    else {
                        teamA.get(i).slowdown();
                    }
                }
            }
        }
        //Do the same thing of we are not on pause.
        //TODO pretty sure this is useless repetition. Delete if that is the case.
        else {
            for (int i = 0; i <= PLAYERCOUNT; i++) {
                if (teamA.get(i).hasBall) {
                    if(!teamA.get(i).isCharging) {
                        teamA.get(i).charge();
                    }
                    else {
                        teamA.get(i).slowdown();
                    }
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

        if(Math.abs(velocityX)>Math.abs(velocityY)) {
            if (rugbytouch.Paused) {

                rugbytouch.Paused = false;

                for (int i = 0; i <= PLAYERCOUNT; i++) {

                    //ONly the ball carrier can pass it.
                    if (teamA.get(i).hasBall) {
                        //Before starting the ball movement, we must end collision events woth the player by setting it's bounds temporarly to an empty rectangle.
                        //TODO find a better way to pass the ball and keep player "solid" probably using box2d
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
            //same exact thing if we are not in pause.
            //TODO check if useless and delete if that is the case.
            else {
                for (int i = 0; i <= PLAYERCOUNT; i++) {
                    if (teamA.get(i).hasBall) {
                        teamA.get(i).setBounds(new Rectangle(0, 0, 0, 0));
                        if (!teamA.get(i).plaqued) {
                            if(velocityX>0 && velocityX<5000) {
                                if(i==PLAYERCOUNT)
                                    teamA.get(i).hasBall = false;
                                ball.pass(true, teamA.get(i).isCharging, false);
                                if(i==0){
                                    teamA.get(i).hasBall = false;
                                    teamA.get(i+1).hasBall = false;
                                }
                            }
                            else if(velocityX>0 && velocityX>5000) {
                                if(i==PLAYERCOUNT)
                                    teamA.get(i).hasBall = false;
                                ball.pass(true, teamA.get(i).isCharging, true);
                                if(i==0){
                                    teamA.get(i).hasBall = false;
                                    teamA.get(i+1).hasBall = false;
                                }
                            }
                            else if(velocityX<0 && velocityX>-5000) {
                                if(i==PLAYERCOUNT)
                                    teamA.get(i).hasBall = false;
                                ball.pass(false, teamA.get(i).isCharging, false);
                                if(i==0){
                                    teamA.get(i).hasBall = false;
                                    teamA.get(i+1).hasBall = false;
                                }
                            }
                            else if(velocityX<0 && velocityX<-5000) {
                                if(i==PLAYERCOUNT)
                                    teamA.get(i).hasBall = false;
                                ball.pass(false, teamA.get(i).isCharging, true);
                                if(i==0){
                                    teamA.get(i).hasBall = false;
                                    teamA.get(i+1).hasBall = false;
                                }
                            }
                            else {
                                //No horizontal direction = no pass
                            }
                        }
                    }
                }
            }
        }
        else {
            //ignore up and down flings
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
