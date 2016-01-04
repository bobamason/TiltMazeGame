package org.masonapps.tiltmazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by ims_3 on 10/29/2015.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private final Color bgColor;
    private MazeGame game;
    private Table table;
    private Skin skin;

    public MenuScreen(MazeGame game) {
        this.game = game;
        bgColor = new Color(Color.BLACK);
        table = new Table();
//        table.debug();
        stage = new Stage(new ScreenViewport());
        table.setFillParent(true);
        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        final Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
        table.add(new Label("Choose Maze Difficulty", labelStyle)).expandX().center().colspan(4).row();
        int s = 7;
        final TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        skin.add("textButtonBg", getTextButtonTexture(), Texture.class);
        textButtonStyle.font = game.font;
        textButtonStyle.up = skin.newDrawable("textButtonBg");
        textButtonStyle.down = skin.newDrawable("textButtonBg", Color.GRAY);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                s += 2;
                final int size = s;
                final TextButton button = new TextButton(s + " x " + s, textButtonStyle);
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        game.startMaze(size);
                    }
                });
                final Cell<TextButton> cell = table.add(button).expandX().center().pad(12);
                if (c == 3) cell.row();
            }
        }
    }

    private Texture getTextButtonTexture() {
        Pixmap pixmap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        pixmap.setColor(Color.NAVY);
        pixmap.fill();
        return new Texture(pixmap);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        skin.dispose();
    }
}
