package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 11/12/2016.
 *
 * This class handles the ball. The ball moves horizontally when passed by the players.
 * TODO : clarify pass movement values to be more precise and look more realistic.
 * TODO : handle the case of the ball being kicked.
 *
 */

public class Ball {

    public int MOVEMENT = 0;
    public int GRAVITY = -15;

    private Vector3 position;
    private Vector3 velocity;

    private Sound passSound; //SOund FX when ball is tossed around

    private Texture texture;
    public boolean dead;

    public void setMOVEMENT(int MOVEMENT) {
        this.MOVEMENT = MOVEMENT;
    }

    public void setGRAVITY(int GRAVITY) {
        this.GRAVITY = GRAVITY;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }

    private Rectangle bounds;

    public Ball(int x, int y) {
        dead = false;
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        texture = new Texture("ball.png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        passSound = Gdx.audio.newSound(Gdx.files.internal("pass.ogg"));
    }

    public void update(float dt) {

        velocity.add(0 ,GRAVITY,0);
        velocity.scl(dt);
        position.add(MOVEMENT*dt, velocity.y, 0);
        if(position.y<0) {
            position.y = 0;
            this.setMOVEMENT(0);
            dead = true;
        }
        velocity.scl(1/dt);
        bounds.setPosition(position.x, position.y);
    }

    public void pass(boolean direction, boolean charging, boolean isSautee) {

        /*The pass is called by a player on the ball and the ball starts to move.
        *Dependng on the user fling direction the pass can go left or right.
        * Depending on the speed of the player holding the ball, the values of movement must be adjusted to avoind collinding with the next player or ennemy.
        * */

        if(rugbytouch.rugbysave.getBoolean("FxOn"))
            passSound.play(0.5f);

        if(direction) {
            //pass right
            if(charging) {
                if(!isSautee) {
                    //simple pass to the right at high speed
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(150);
                }
                if(isSautee) {
                    //long pass to the right at high speed
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(300);
                }
            }
            else {
                if(isSautee) {
                    //long pass to the right at normal speed
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(300);
                }
                if(!isSautee) {
                    //normal pss to the right at normal speed
                    velocity.y = 100;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(150);
                }
            }
        }
        if(!direction) {
            //pass left, compensate for ball position not centered on player
            if(charging) {
                if(!isSautee) {
                    //noormal pass to the left at high speed
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-200);
                }
                if(isSautee) {
                    //long pass to the left at high speed
                    velocity.y = 350;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-350);
                }
            }
            else {
                if(!isSautee) {
                    //normal pass to the left at normal speed
                    velocity.y = 100;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-200);
                }
                if(isSautee) {
                    //long pass to the left at high speed
                    velocity.y = 300;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-330);
                }
            }
        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {return new TextureRegion(texture);}

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
}
