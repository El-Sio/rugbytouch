package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Sprites.Ball;
import com.mygdx.game.Sprites.EnnemyPlayer;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.rugbytouch;

import static sun.audio.AudioPlayer.player;

/**
 * Created by charl on 11/12/2016.
 */

public class PlayState extends State {

    private Ball ball;
    private Array<Player> teamA;
    private Array<EnnemyPlayer> teamB;
    private static final int PLAYERCOUNT = 4;
    private Texture background;

    public PlayState(GameStateManager gsm) {

        super(gsm);
        cam.setToOrtho(false, rugbytouch.WIDTH, rugbytouch.HEIGHT);
        background = new Texture("terrain.png");
        teamA = new Array<Player>(PLAYERCOUNT);
        teamB = new Array<EnnemyPlayer>(PLAYERCOUNT);
        for(int i=0; i<=PLAYERCOUNT; i++) {
            teamA.add(new Player(100*(i+1), 300 - (i+1)*100 ));
            teamA.get(i).hasBall = false;
            if(i!=PLAYERCOUNT) {
            teamB.add(new EnnemyPlayer(100*(i+1), 700));}
        }

        ball = new Ball(100,400);
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()) {
            for(int i  = 0; i<=PLAYERCOUNT; i++)
            {
                if(teamA.get(i).hasBall)
                {
                    teamA.get(i).setBounds(new Rectangle(0,0,0,0));
                    teamA.get(i).hasBall = false;
                    if(!teamA.get(i).plaqued)
                    {
                        ball.pass();
                    }
                }
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        for(int i = 0; i<=PLAYERCOUNT; i++) {
            teamA.get(i).update(dt);
            if(teamA.get(i).getPosition().y > rugbytouch.HEIGHT - teamA.get(i).getTexture().getRegionHeight() && teamA.get(i).hasBall)
            {
                System.out.println("essai !");
                gsm.set(new PlayState(gsm));
            }
            if(i!=PLAYERCOUNT) {teamB.get(i).update(dt);}
            if(teamA.get(i).collide(ball.getBounds())) {
                teamA.get(i).hasBall = true;
                ball.setPosition(new Vector3(teamA.get(i).getPosition().x + teamA.get(i).getTexture().getRegionWidth() /2, teamA.get(i).getPosition().y + teamA.get(i).getTexture().getRegionHeight() /2, 0));
                ball.setMOVEMENT(0);
                ball.setGRAVITY(0);
                ball.setVelocity(new Vector3(0,0,0));
            }
            if(i!=PLAYERCOUNT) {
                if (teamA.get(i).collide(teamB.get(i).getBounds())) {
                    teamA.get(i).setMOVEMENT(0);
                    teamA.get(i).setVelocity(new Vector3(0, 0, 0));
                    teamB.get(i).setMOVEMENT(0);
                    if (teamA.get(i).hasBall) {
                        System.out.println("plaqué !");
                        teamA.get(i).plaqued = true;
                        gsm.set(new PlayState(gsm));
                    }
                }
            }
        }

        ball.update(dt);
        cam.position.x = ball.getPosition().x;
        cam.position.y = ball.getPosition().y;
        if(ball.dead) {

            gsm.set(new PlayState(gsm));
        }
        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {

        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background,0,0);
        sb.draw(ball.getTexture(), ball.getPosition().x, ball.getPosition().y);
        for(int i = 0; i<=PLAYERCOUNT; i++) {
            sb.draw(teamA.get(i).getTexture(), teamA.get(i).getPosition().x, teamA.get(i).getPosition().y);
            if(i!=PLAYERCOUNT) {
                sb.draw(teamB.get(i).getTexture(), teamB.get(i).getPosition().x, teamB.get(i).getPosition().y);
            }
        }
        sb.end();
    }

    @Override
    public void dispose() {

        for(int i = 0; i<=PLAYERCOUNT; i++) {
            teamA.get(i).dispose();
            if(i!=PLAYERCOUNT) {
                teamB.get(i).dispose();
            }
        }
        ball.dispose();
        background.dispose();

    }
}
