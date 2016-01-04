package org.masonapps.tiltmazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by ims_3 on 10/15/2015.
 */
public class LoadingScreen implements Screen {


    private MazeGame game;
    private Color bgColor;
    private Texture texture;
    private static final Vector2 pos = new Vector2();
    private static final Vector2 dimen = new Vector2();

    public LoadingScreen(MazeGame game) {
        this.game = game;
        bgColor = new Color(Color.BLACK);
        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        invalidateDimensions(width, height);
    }

    private void invalidateDimensions(int width, int height) {
        final float min = Math.min(width * 0.5f, height * 0.5f);
        pos.set(width / 2 - min / 2, height / 2 - min / 2);
        dimen.set(min, min);
    }

    @Override
    public void show() {
        texture = new Texture("badlogic.jpg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        game.spriteBatch.begin();
        game.spriteBatch.draw(texture, pos.x, pos.y, dimen.x, dimen.y);
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        invalidateDimensions(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
