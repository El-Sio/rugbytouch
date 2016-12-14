package com.mygdx.game.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by charl on 11/12/2016.
 */

public class OptionState extends State {
    public OptionState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    protected void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {

    }

    @Override
    public void dispose() {

    }
}
