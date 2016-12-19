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
 */

public class Ball {

    public int MOVEMENT = 0;
    public int GRAVITY = -15;

    private Vector3 position;
    private Vector3 velocity;

    private Sound passSound;

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

        if(rugbytouch.rugbysave.getBoolean("FxOn"))
            passSound.play(0.5f);

        if(direction) {
            //pass right
            if(charging) {
                if(!isSautee) {
                    System.out.println("passe à droite simple en courant");
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(150);
                }
                if(isSautee) {
                    System.out.println("passe à droite sautée en courant");
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(300);
                }
            }
            else {
                if(isSautee) {
                    System.out.println("passe à droite sautée sans courir");
                    velocity.y = 250;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(300);
                }
                if(!isSautee) {
                    System.out.println("passe à droite simple sans courir");
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
                    System.out.println("passe à gauche simple en courant");
                    velocity.y = 200;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-232);
                }
                if(isSautee) {
                    System.out.println("passe à gauche sautée en courant");
                    velocity.y = 400;
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-462);
                }
            }
            else {
                if(!isSautee) {
                    System.out.println("passe à gauche simple sans courir");
                    velocity.y = (100);
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-200);
                }
                if(isSautee) {
                    System.out.println("passe à gauche sautée sans courir");
                    velocity.y = (250);
                    this.setGRAVITY(-15);
                    this.setMOVEMENT(-350);
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
