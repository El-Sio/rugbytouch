package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.rugbytouch;

/**
 * Created by charl on 13/12/2016.
 * This class handles ennemy players
 * TODO : have ennemy players do cooler stuff than just block your way.
 */

public class EnnemyPlayer {

    private Texture playerImg;
    private Vector3 velocity;
    private Vector3 position;
    private int MOVEMENT = -50;
    private boolean VISIBLE;
    private Animation ennemyAnimation;

    public static boolean hasBall;

    private Rectangle bounds;

    public int getMOVEMENT() {
        return MOVEMENT;
    }

    public EnnemyPlayer(int x, int y, boolean visible) {

        //EnnemyPLayer is a simpler type of player that can be rendered with an "empty" texture to allow for a "gap" in the defense.

        position = new Vector3(x,y,0);
        velocity = new Vector3(0,0,0);
        int i = rugbytouch.rugbysave.getInteger("team");
        if(visible) {
            if (i == 0)
                playerImg = new Texture("player[" + (i + 1) + "]anim.png");
            else
                playerImg = new Texture("player[" + (i - 1) + "]anim.png");

            ennemyAnimation = new Animation(new TextureRegion(playerImg), 4, 0.5f);
            bounds = new Rectangle(x, y, playerImg.getWidth()/4, playerImg.getHeight());
        }
        if(!visible) {
            playerImg = new Texture("vide.png");
            ennemyAnimation = new Animation(new TextureRegion(playerImg),1,0.5f);
            bounds = new Rectangle(0,0,0,0);
        }
    }

    public boolean collide(Rectangle ball) {
        return ball.overlaps(bounds);
    }

    public Rectangle getBounds() {return bounds;}

    public void update(float dt) {

        ennemyAnimation.update(dt);
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

    public TextureRegion getTexture() {return ennemyAnimation.getFrame();}

    public void setMOVEMENT(int MOVEMENT) {
        this.MOVEMENT = MOVEMENT;
    }
}