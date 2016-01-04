package org.masonapps.tiltmazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.utils.Array;

import org.masonapps.tiltmazegame.bullet.BaseBulletScreen;
import org.masonapps.tiltmazegame.bullet.BulletConstructor;
import org.masonapps.tiltmazegame.bullet.BulletEntity;

import java.util.Stack;

/**
 * Created by ims_3 on 10/15/2015.
 */
public class GameScreen extends BaseBulletScreen implements GestureDetector.GestureListener {

    final MazeGame game;
    private static final String CUBE = "cube";
    private static final String FLAT_BOX = "flatBox";
    private static final String BALL = "ball";
    private static final float MAX_CAMERA_SPEED = 10f;
    private final Vector3 ballPosition = new Vector3();
    private final Vector3 startPos = new Vector3();
    private final Vector3 endPos = new Vector3();
    private final GestureDetector gestureDetector;
    private BulletEntity ball = null;
    private final Vector3 gravity = new Vector3();
    private int size = 9;
    private static final Matrix4 tempM = new Matrix4();
    private static final Vector3 tempV = new Vector3();
    private static final Quaternion rotation = new Quaternion();
    private float theta = 0;
    private PointLight pointLight;
    private float cameraDist = 4f;
    private float lastDist = 1f;
    private Vector2 offset = new Vector2();
    private static final Vector2 tempV2 = new Vector2();

    public GameScreen(MazeGame game) {
        super(game);
        this.game = game;
        game.camera3D.far = 60f;
        game.camera3D.near = 0.2f;
        game.camera3D.update();

        gestureDetector = new GestureDetector(this);

        pointLight = new PointLight();
        pointLight.setColor(Color.CYAN);
        pointLight.setIntensity(0.5f);
        environment.add(pointLight);

        world.addConstructor(CUBE, new BulletConstructor(game.cube, 0, new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        world.addConstructor(FLAT_BOX, new BulletConstructor(game.flatBox, 0, new btBoxShape(new Vector3(0.5f, 0.05f, 0.5f))));
        world.addConstructor(BALL, new BulletConstructor(game.sphere, 0.5f, new btSphereShape(0.3f)));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gestureDetector);
//        setupMaze(size, size);
    }

    private void setupMaze(int width, int height) {
        int[][] maze = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1;
            }
        }
        int r = 0;
        int c = 0;
        while (r % 2 == 0) {
            r = MathUtils.random(1, height - 1);
        }
        while (c % 2 == 0) {
            c = MathUtils.random(1, width - 1);
        }

        startPos.set(r, 0.5f, c);

        generateDFSPaths(r, c, maze);


        int endRow, endCol;
        do {
            endRow = MathUtils.random(1, height - 1);
            endCol = MathUtils.random(1, width - 1);

        }
        while (maze[endRow][endCol] != 0 || (endRow - r) * (endRow - r) + (endCol - c) * (endCol - c) < 4 * 4);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == endRow && j == endCol) {
                    endPos.set(j, 0.2f, i);
                } else {
                    if (maze[i][j] == 1)
                        world.add(CUBE, j, 0.5f, i);
                    else
                        world.add(FLAT_BOX, j, 0.05f, i);
                }
            }
        }
        ball = world.add(BALL, startPos.x, startPos.y, startPos.z);
        ball.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        ball.body.setFriction(30f);
        ball.isShadeless = true;
    }

    private void generateDFSPaths(int row, int col, int[][] maze) {
        int[] directions = new int[4];
        Stack<Coord> stack = new Stack<Coord>();
        stack.add(new Coord(row, col));
        int r, c;
        while (stack.size() > 0) {
            generateRandomDirections(directions);
            final Coord coord = stack.pop();
            r = coord.r;
            c = coord.c;
            for (int i = 0; i < 4; i++) {
                switch (directions[i]) {
                    //up
                    case 1:
                        if (r - 2 <= 0) continue;
                        if (maze[r - 2][c] != 0) {
                            maze[r - 1][c] = 0;
                            maze[r - 2][c] = 0;
                            stack.add(new Coord(r - 2, c));
                        }
                        break;
                    //down
                    case 2:
                        if (r + 2 >= maze.length - 1) continue;
                        if (maze[r + 2][c] != 0) {
                            maze[r + 1][c] = 0;
                            maze[r + 2][c] = 0;
                            stack.add(new Coord(r + 2, c));
                        }
                        break;
                    //left
                    case 3:
                        if (c - 2 <= 0) continue;
                        if (maze[r][c - 2] != 0) {
                            maze[r][c - 1] = 0;
                            maze[r][c - 2] = 0;
                            stack.add(new Coord(r, c - 2));
                        }
                        break;
                    //right
                    case 4:
                        if (c + 2 >= maze[0].length - 1) continue;
                        if (maze[r][c + 2] != 0) {
                            maze[r][c + 1] = 0;
                            maze[r][c + 2] = 0;
                            stack.add(new Coord(r, c + 2));
                        }
                        break;
                }
            }
        }
    }

    private void generateRandomDirections(int[] dirs) {
        Array<Integer> randoms = new Array<Integer>(4);
        for (int i = 0; i < 4; i++) {
            randoms.add(i + 1);
        }
        randoms.shuffle();
        for (int i = 0; i < 4; i++) {
            dirs[i] = randoms.get(i);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!Gdx.input.isTouched() && !offset.isZero()) {
            offset.add(tempV2.set(offset).scl(-1f).limit(MAX_CAMERA_SPEED).scl(deltaTime));
        }
        ball.transform.getTranslation(ballPosition);
        game.camera3D.position.set(ballPosition.x + offset.x, cameraDist, ballPosition.z + cameraDist / 2f + offset.y);
        game.camera3D.up.set(Vector3.Y);
        game.camera3D.lookAt(ballPosition.x + offset.x, ballPosition.y, ballPosition.z + offset.y);
        game.camera3D.update();

        pointLight.setPosition(ballPosition);
        gravity.set(Gdx.input.getAccelerometerY(), -10f, Gdx.input.getAccelerometerX()).rotate(Vector3.X, MathUtils.atan2(cameraDist / 2f, cameraDist) * MathUtils.radiansToDegrees / 2);
        ((btDynamicsWorld) world.collisionWorld).setGravity(gravity);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        checkEndReached(ballPosition);

//        game.drawText("", 0f, 0f);

        game.modelBatch.begin(game.camera3D);
        tempV.set(game.camera3D.direction).scl(game.camera3D.far - 0.5f).add(game.camera3D.position);
        game.bgPlaneInstance.transform.idt().translate(tempV).rotate(Vector3.Y, tempV.set(game.camera3D.direction).scl(-1)).scale(2, 1, 2);
        game.modelBatch.render(game.bgPlaneInstance);

        tempM.idt().translate(endPos.x, endPos.y, endPos.z).scale(2.0f, 2.0f, 2.0f);
        game.particleEffect.setTransform(tempM);
        game.particleSystem.begin();
        game.particleSystem.updateAndDraw();
        game.particleSystem.end();
        game.modelBatch.render(game.particleSystem);

        game.modelBatch.end();
        theta += delta * 270f;
        theta %= 360f;
    }

    private void checkEndReached(Vector3 pos) {
        if (pos.y < -5f) {
            reset();
        }
    }

    private void reset() {
        world.clearWorld();
        setupMaze(size, size);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        world.clearWorld();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (Math.abs(distance - initialDistance) < 5f) lastDist = distance;
        cameraDist = Math.min(size, Math.max(2f, cameraDist * (lastDist / distance)));
        lastDist = distance;
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
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
        final float scale = ((float) size) / Gdx.graphics.getHeight();
        offset.add(-deltaX * scale, -deltaY * scale);
        offset.x = Math.min(size - ballPosition.x, Math.max(offset.x, -ballPosition.x));
        offset.y = Math.min(size - ballPosition.z - cameraDist / 2, Math.max(offset.y, -ballPosition.z - cameraDist / 2));
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    public void setSize(int size) {
        this.size = size;
        reset();
    }

    private class Coord {
        int r, c;

        public Coord(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }
}
