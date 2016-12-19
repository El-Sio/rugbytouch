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
 */

public class PlayState extends State implements GestureDetector.GestureListener {

    private Ball ball;
    private Array<Player> teamA;
    private Array<EnnemyPlayer> teamB;
    private static final int PLAYERCOUNT = 4;
    private Texture background;
    private int LIVES;
//    private int LEVEL;
    private Random rand;
    private int position;
    private int positionattaque;
    private Array<Texture> lifeArray;

    public PlayState(GameStateManager gsm,int lives) {

        super(gsm);
        LIVES = lives;
        rand = new Random();
        position = rand.nextInt(PLAYERCOUNT+1);
        positionattaque = rand.nextInt(PLAYERCOUNT+1);

        lifeArray = new Array<Texture>(LIVES);
        for(int i = 0; i<=LIVES; i++) {
            lifeArray.add(new Texture("ball.png"));
        }
        cam.setToOrtho(false, rugbytouch.WIDTH, rugbytouch.HEIGHT);
        background = new Texture("terrain.png");

        teamA = new Array<Player>(PLAYERCOUNT);
        teamB = new Array<EnnemyPlayer>(PLAYERCOUNT);

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

        ball = new Ball(100+ 100*(positionattaque),400);
        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();

        if(!rugbytouch.Paused) {

            for (int i = 0; i <= PLAYERCOUNT; i++) {
                teamA.get(i).update(dt);
                if (ball.getPosition().y > rugbytouch.HEIGHT +20 && teamA.get(i).hasBall) {
//                    System.out.println("essai !");
                    if(rugbytouch.rugbysave.getBoolean("FxOn"))
                        teamA.get(i).essaiSound.play();
                    if(LIVES<3) {LIVES++;}
                    gsm.set(new PlayState(gsm, LIVES));
                }

                teamB.get(i).update(dt);

                if (teamA.get(i).collide(ball.getBounds())) {
                    teamA.get(i).hasBall = true;
                    if(i>1) {
                        teamA.get(i - 1).hasBall = false;
                        teamA.get(i-2).hasBall = false;
                    }
                    if(i<PLAYERCOUNT-1) {
                        teamA.get(i + 1).hasBall = false;
                        teamA.get(i+2).hasBall = false;
                    }
                    ball.setPosition(new Vector3(teamA.get(i).getPosition().x + teamA.get(i).getTexture().getRegionWidth() / 2, teamA.get(i).getPosition().y + teamA.get(i).getTexture().getRegionHeight() / 2, 0));
                    ball.setMOVEMENT(0);
                    ball.setGRAVITY(0);
                    ball.setVelocity(new Vector3(0, 0, 0));
                }

                if(teamB.get(i).collide(ball.getBounds()))
                {
//                    System.out.println("balle perdue");
                    ball.dead = true;
                }

                if (teamA.get(i).collide(teamB.get(i).getBounds())) {
/*                    teamA.get(i).setMOVEMENT(0);
                    teamA.get(i).setVelocity(new Vector3(0, 0, 0));
                    teamB.get(i).setMOVEMENT(0); */
                    if (teamA.get(i).hasBall) {
//                        System.out.println("plaqué !");
                        if (rugbytouch.rugbysave.getBoolean("FxOn"))
                            teamA.get(i).plaquedSound.play();
                        teamA.get(i).plaqued = true;
                        LIVES--;
                        if(LIVES == 0) {
                            gsm.set(new MenuState(gsm));
                        }
                        if(LIVES >0) {
                            gsm.set(new PlayState(gsm, LIVES));
                        }
                    }
                }
                if(!teamA.get(i).hasBall && teamA.get(i).getPosition().y > teamB.get(i).getPosition().y + teamB.get(i).getTexture().getRegionHeight() + 10 && i!=PLAYERCOUNT) {
                    if(i==0 && teamA.get(1).hasBall)
                        teamA.get(i).setBounds(new Rectangle(teamA.get(i).getPosition().x, teamA.get(i).getPosition().y, teamA.get(i).getTexture().getRegionWidth(), teamA.get(i).getTexture().getRegionHeight()));
                    else if(i!=0)
                        teamA.get(i).setBounds(new Rectangle(teamA.get(i).getPosition().x, teamA.get(i).getPosition().y, teamA.get(i).getTexture().getRegionWidth(), teamA.get(i).getTexture().getRegionHeight()));
                }
            }

            ball.update(dt);
            cam.position.x = ball.getPosition().x;
            cam.position.y = ball.getPosition().y;
            if (ball.dead) {

                LIVES --;
                if(LIVES == 0) {
                    gsm.set(new MenuState(gsm));
                }
                if(LIVES >0) {
                    gsm.set(new PlayState(gsm, LIVES));
                }
            }
            cam.update();
        }
    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background,0,0);

        for(int i = 0; i<=PLAYERCOUNT; i++) {
            sb.draw(teamA.get(i).getTexture(), teamA.get(i).getPosition().x, teamA.get(i).getPosition().y);
            sb.draw(teamB.get(i).getTexture(), teamB.get(i).getPosition().x, teamB.get(i).getPosition().y);
        }
        sb.draw(ball.getTexture(), ball.getPosition().x, ball.getPosition().y);

        for (int i=0; i<=LIVES; i++) {
            sb.draw(lifeArray.get(i), cam.position.x + cam.viewportWidth/2 - 42*i, cam.position.y - cam.viewportHeight/2);
        }

        sb.end();
    }

    @Override
    public void dispose() {

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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        if(rugbytouch.Paused) {
            rugbytouch.Paused = false;
//            System.out.println("unpaused");
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

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        if(Math.abs(velocityX)>Math.abs(velocityY)) {
 //           System.out.println(velocityX);
            if (rugbytouch.Paused) {

                rugbytouch.Paused = false;
                System.out.println("unpaused");

                for (int i = 0; i <= PLAYERCOUNT; i++) {
                    //Pass only if not charging
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
                                if(i==0) {
                                    teamA.get(i).hasBall = false;
                                    teamA.get(i+1).hasBall = false;
                                }
                            }
                            else {
                                //no horizontal direction = no pass
                            }
                        }
                    }
                }
            }
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
