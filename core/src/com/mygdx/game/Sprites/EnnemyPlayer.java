package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 13/12/2016.
 */

public class EnnemyPlayer {

    private Texture playerImg;
    private Vector3 velocity;
    private Vector3 position;
    private int MOVEMENT = -50;
    private boolean VISIBLE;

    public static boolean hasBall;

    private Rectangle bounds;

    public EnnemyPlayer(int x, int y, boolean visible) {
        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        int i = rugbytouch.rugbysave.getInteger("team");
        if(visible) {
            if (i == 0)
                playerImg = new Texture("player[" + (i + 1) + "].png");
            else
                playerImg = new Texture("player[" + (i - 1) + "].png");

            bounds = new Rectangle(x, y, playerImg.getWidth(), playerImg.getHeight());
        }
        if(!visible) {
            playerImg = new Texture("vide.png");
            bounds = new Rectangle(0,0,0,0);
        }
    }

    public boolean collide(Rectangle ball) {
        return ball.overlaps(bounds);
    }

    public Rectangle getBounds() {return bounds;}

    public void update(float dt) {

        position.add(0,MOVEMENT*dt,0);
        if(position.y<0)
            position.y = 0;
        bounds.setPosition(position.x, position.y);
    }

    public void dispose() {
        playerImg.dispose();
    }

    public Vector3 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {return new TextureRegion(playerImg);}

    public void setMOVEMENT(int MOVEMENT) {
        this.MOVEMENT = MOVEMENT;
    }
}