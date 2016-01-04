package org.masonapps.tiltmazegame.bullet;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ims_3 on 10/15/2015.
 */
public abstract class BaseBulletGame extends Game {

    public PerspectiveCamera camera3D;
    public ModelBatch modelBatch;
    public Array<Disposable> disposables = new Array<Disposable>();
    public ModelBuilder modelBuilder;

    @Override
    public void create() {
        modelBuilder = new ModelBuilder();
        modelBatch = new ModelBatch();
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        if (width > height)
            camera3D = new PerspectiveCamera(67, 1f * width / height, 1f);
        else
            camera3D = new PerspectiveCamera(67, 1f, 1f * height / width);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Disposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
        modelBatch.dispose();
        modelBatch = null;
    }
}
