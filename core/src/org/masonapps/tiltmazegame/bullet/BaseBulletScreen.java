package org.masonapps.tiltmazegame.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;


/**
 * Created by ims_3 on 10/15/2015.
 */
public abstract class BaseBulletScreen implements Screen {
    public final BaseBulletGame bulletGame;
    public Environment environment;
    public BaseLight light;
    public BulletWorld world;
    public Color skyColor;

    public BaseBulletScreen(BaseBulletGame game) {
        init();
        bulletGame = game;
        skyColor = new Color(Color.DARK_GRAY);
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.DARK_GRAY));
//        light = new PointLight();
//        ((PointLight)light).set(Color.WHITE, Vector3.Z.scl(4), 100f);
        light = new DirectionalLight();
        ((DirectionalLight) light).set(Color.WHITE, new Vector3(0.5f, -0.5f, 0.5f).nor());
        environment.add(light);
        world = createWorld();
        bulletGame.camera3D.position.set(10f, 10f, 10f);
        bulletGame.camera3D.lookAt(Vector3.Zero);
        bulletGame.camera3D.update();
    }

    public static void init() {
        Bullet.init();
    }

    public BulletWorld createWorld() {
        return new BulletWorld();
    }

    @Override
    public void render(float delta) {
        update(delta);
        beginRender();
        renderWorld();
    }

    @Override
    public void dispose() {
        world.dispose();
        world = null;
    }

    public void update(float deltaTime) {
        world.update(deltaTime);
    }

    protected void beginRender() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(skyColor.r, skyColor.g, skyColor.b, skyColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    public void renderWorld() {
        bulletGame.modelBatch.begin(bulletGame.camera3D);
        world.render(bulletGame.modelBatch, environment);
        bulletGame.modelBatch.end();
    }
}