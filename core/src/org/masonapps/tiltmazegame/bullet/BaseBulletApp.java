package org.masonapps.tiltmazegame.bullet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by ims_3 on 8/11/2015.
 */
public class BaseBulletApp extends ApplicationAdapter implements GestureDetector.GestureListener {

    public PerspectiveCamera camera;
    public Environment environment;
    public BaseLight light;
    public BulletWorld world;
    public ModelBatch modelBatch;
    public Array<Disposable> disposables = new Array<Disposable>();
    public ModelBuilder modelBuilder;
    public float deltaTime;
    public Color skyColor;

    public static void init() {
        Bullet.init();
    }

    public BulletWorld createWorld() {
        return new BulletWorld();
    }

    @Override
    public void create() {
        init();
        skyColor = new Color(Color.DARK_GRAY);
        modelBuilder = new ModelBuilder();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.DARK_GRAY));
//        light = new PointLight();
//        ((PointLight)light).set(Color.WHITE, Vector3.Z.scl(4), 100f);
        light = new DirectionalLight();
        ((DirectionalLight) light).set(Color.WHITE, new Vector3(0.5f, -0.5f, 0.5f).nor());
        environment.add(light);
        modelBatch = new ModelBatch();
        world = createWorld();
        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        if (width > height)
            camera = new PerspectiveCamera(67, 1f * width / height, 1f);
        else
            camera = new PerspectiveCamera(67, 1f, 1f * height / width);
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(Vector3.Zero);
        camera.update();
    }

    @Override
    public void dispose() {
        world.dispose();
        world = null;
        for (Disposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
        modelBatch.dispose();
        modelBatch = null;
        super.dispose();
    }

    @Override
    public void render() {
        render(true);
    }

    public void render(boolean update) {
        deltaTime = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        if (update) update();
        beginRender();
        renderWorld();
    }

    public void update() {
        world.update(deltaTime);
    }

    protected void beginRender() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(skyColor.r, skyColor.g, skyColor.b, skyColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void renderWorld() {
        modelBatch.begin(camera);
        world.render(modelBatch, environment);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
