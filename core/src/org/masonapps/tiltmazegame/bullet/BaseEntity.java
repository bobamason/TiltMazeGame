package org.masonapps.tiltmazegame.bullet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

public abstract class BaseEntity implements Disposable {
    public Matrix4 transform;
    public ModelInstance modelInstance;
    public float radius;
    public final Vector3 center = new Vector3();
    private static final Vector3 temp = new Vector3();
    private static final BoundingBox boundingBox = new BoundingBox();
    public boolean isShadeless = false;

    public BaseEntity(ModelInstance modelInstance, Matrix4 transform) {
        this.transform = transform;
        this.modelInstance = modelInstance;
        modelInstance.calculateBoundingBox(boundingBox);
        boundingBox.getCenter(center);
        boundingBox.getDimensions(temp);
        this.radius = temp.len() / 2f;
    }

    public boolean isVisible(Camera camera) {
        transform.getTranslation(temp);
        temp.add(center);
        return camera.frustum.sphereInFrustum(temp, radius);
    }
}
