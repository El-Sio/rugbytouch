package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 11/12/2016.
 *
 * THhis class handles the player controlled by the user. Player can run, slowdown and pass the ball lefto or right.
 * TODO allow the player to kick over the defense or try to go through by creating a "ruck"
 *
 */

public class Player {

    private int MOVEMENT = 100; //Used for the vertical "speed" of the player
    private int GRAVITY = 0;

    private Vector3 position;
    private Vector3 velocity;

    private Texture texture;

    public boolean hasBall; // is the player currently holding the ball
    public boolean plaqued; // has the player been tackled
    public Sound plaquedSound; //Sound FX when tackled
    public Sound essaiSound; //Sound FX when scoring

    private Animation playerAnimation; //Animated sprite

    public boolean isCharging; //IS the player accelerating ?

    public void setMOVEMENT(int MOVEMENT) {
        this.MOVEMENT = MOVEMENT;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }

    private Rectangle bounds;

    public Player(int x, int y) {
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        essaiSound = Gdx.audio.newSound(Gdx.files.internal("clap.mp3"));
        essaiSound.setVolume(0,5f);
        plaquedSound = Gdx.audio.newSound(Gdx.files.internal("plaqued.wav"));
        plaquedSound.setVolume(0,5f);
        int i = rugbytouch.rugbysave.getInteger("team");
        texture = new Texture("player["+i+"]anim.png");
        playerAnimation = new Animation(new TextureRegion(texture), 4, 0.5f);
        bounds = new Rectangle(x, y, texture.getWidth()/4, texture.getHeight());
        hasBall = false;
        plaqued = false;
        isCharging = false;
    }

    public void update(float dt) {

        playerAnimation.update(dt);
        velocity.scl(dt);
        position.add(0,MOVEMENT*dt + velocity.y,0);
        velocity.scl(1/dt);
        if(position.y>rugbytouch.HEIGHT) {
            this.setMOVEMENT(0);
        }
        bounds.setPosition(position.x, position.y);
    }

    public boolean collide(Rectangle ball) {
        return ball.overlaps(bounds);
    }

    public void charge() {
        //Set speed up a notch
        if(!isCharging) {
            isCharging = true;
            velocity.y = 150;
        }
    }

    public void slowdown() {
        //Set speed back to original values
        isCharging = false;
        velocity.y = 0;
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {return playerAnimation.getFrame();}

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
