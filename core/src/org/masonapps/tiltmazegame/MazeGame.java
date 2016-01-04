package org.masonapps.tiltmazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.MathUtils;

import org.masonapps.tiltmazegame.bullet.BaseBulletGame;

public class MazeGame extends BaseBulletGame {

    private static final String FLOOR_TEXTURE = "textures/metal_maze_floor.png";
    private static final String WALL_TEXTURE = "textures/metal_square.png";
    private static final String BACKGROUND_TEXTURE = "textures/background.jpg";
    private static final String PARTICLE_EFFECT = "particle_fx/dust.pfx";
    public AssetManager assets;
    public boolean loaded;
    public ModelInstance bgPlaneInstance;
    public ParticleSystem particleSystem;
    public ParticleEffect particleEffect;
    public Model cube;
    public Model flatBox;
    public Model sphere;
    private GameScreen gameScreen = null;
    public SpriteBatch spriteBatch;
    public BitmapFont font;
    private PointSpriteParticleBatch particleBatch;

    @Override
    public void create() {
        super.create();
        loaded = false;
        assets = new AssetManager();
        spriteBatch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto_bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParams.size = Math.round(18f * Gdx.graphics.getDensity());
        font = generator.generateFont(fontParams);
        generator.dispose();

        particleSystem = ParticleSystem.get();
        particleBatch = new PointSpriteParticleBatch();
        particleBatch.setCamera(camera3D);
        particleSystem.add(particleBatch);

        assets.load(WALL_TEXTURE, Texture.class);
        assets.load(FLOOR_TEXTURE, Texture.class);
        assets.load(BACKGROUND_TEXTURE, Texture.class);

        ParticleEffectLoader.ParticleEffectLoadParameter params = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader particleLoader = new ParticleEffectLoader(new InternalFileHandleResolver());
        assets.setLoader(ParticleEffect.class, particleLoader);
        assets.load(PARTICLE_EFFECT, ParticleEffect.class, params);
        setScreen(new LoadingScreen(this));
    }

    private void doneLoading() {
        ParticleEffect orgEffect = assets.get(PARTICLE_EFFECT);
        particleEffect = orgEffect.copy();
        particleEffect.init();
        particleEffect.start();
        particleSystem.add(particleEffect);

        float s = (float) Math.tan(Math.toRadians(camera3D.fieldOfView / 2.)) * camera3D.far;
        Model bgPlane = modelBuilder.createRect(
                s, 0, -s,
                -s, 0, -s,
                -s, 0, s,
                s, 0, s,
                0, 1, 0,
                new Material(TextureAttribute.createDiffuse(assets.get(BACKGROUND_TEXTURE, Texture.class))), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        bgPlaneInstance = new ModelInstance(bgPlane);

        final Material wallMaterial = new Material(TextureAttribute.createDiffuse(assets.get(WALL_TEXTURE, Texture.class)), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(12f));
        cube = modelBuilder.createBox(1f, 1f, 1f, wallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        final Material floorMaterial = new Material(TextureAttribute.createDiffuse(assets.get(FLOOR_TEXTURE, Texture.class)), ColorAttribute.createSpecular(Color.WHITE));
        flatBox = modelBuilder.createRect(
                0.5f, 0, -0.5f,
                -0.5f, 0, -0.5f,
                -0.5f, 0, 0.5f,
                0.5f, 0, 0.5f,
                0, 1, 0, floorMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        sphere = modelBuilder.createSphere(0.6f, 0.6f, 0.6f, 32, 16, new Material(TextureAttribute.createDiffuse(createBallTexture())), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        loaded = true;
        gameScreen = new GameScreen(this);
        setScreen(new MenuScreen(this));
    }

    public void startMaze(int size) {
        gameScreen.setSize(size);
        setScreen(gameScreen);
    }

    private Texture createBallTexture() {
        Pixmap ballMap = new Pixmap(64, 64, Pixmap.Format.RGB888);
        float random;
        for (int y = 0; y < 64; y += 2) {
            for (int x = 0; x < 64; x += 2) {
                random = MathUtils.random(0.8f, 1f);
                ballMap.setColor(MathUtils.random(0f, 0.4f), random, random, 1.0f);
                ballMap.fillRectangle(x, y, 2, 2);
            }
        }
        disposables.add(ballMap);
        final Texture texture = new Texture(ballMap);
        disposables.add(texture);
        if (gameScreen != null) gameScreen.dispose();
        return texture;
    }

    @Override
    public void render() {
        if (!loaded && assets.update()) {
            doneLoading();
        }
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (gameScreen != null) gameScreen.dispose();
        assets.dispose();
        spriteBatch.dispose();
        font.dispose();
        particleEffect.end();
        particleBatch.end();
        particleSystem.end();
        particleEffect.dispose();
    }

    public void drawText(String s, float x, float y) {

    }
}
