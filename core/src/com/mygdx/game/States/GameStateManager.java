package com.mygdx.game.States;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

/**
 * Created by charl on 04/12/2016.
 *
 * Copied from the flappy bird demo videos.
 *
 * This class allows the game to "swhitch" from one screen to another on various situations.
 *
 * it's instancied once per game and passed as argument at each change of screen. It's basically a stack of game states
 *
 */

public class GameStateManager {

    private Stack<State> states;

    public GameStateManager() {
       states = new Stack<State>();
    }

    public void push(State state) {
        states.push(state);
    }

    public void pop() {
        states.pop().dispose();
    }

    public void set(State state) {
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt) {
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb) {
        states.peek().render(sb);
    }
}
