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

public class Player {

    private int MOVEMENT = 100;
    private int GRAVITY = 0;

    private Vector3 position;
    private Vector3 velocity;

    private Texture texture;

    public boolean hasBall;
    public boolean plaqued;
    public Sound plaquedSound;
    public Sound essaiSound;

    public boolean isCharging;

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
        essaiSound = Gdx.audio.newSound(Gdx.files.internal("essai.wav"));
        essaiSound.setVolume(0,5f);
        plaquedSound = Gdx.audio.newSound(Gdx.files.internal("plaqued.wav"));
        plaquedSound.setVolume(0,5f);
        int i = rugbytouch.rugbysave.getInteger("team");
        texture = new Texture("player["+i+"].png");
        bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
        hasBall = false;
        plaqued = false;
        isCharging = false;
    }

    public void update(float dt) {

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
        if(!isCharging) {
            isCharging = true;
            velocity.y = 150;
        }
    }

    public void slowdown() {
        isCharging = false;
        velocity.y = 0;
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {return new TextureRegion(texture);}

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
