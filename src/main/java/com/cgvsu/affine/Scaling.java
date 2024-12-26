package com.cgvsu.affine;


import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

import java.util.Objects;

public class Scaling implements AffineTransform {
    private final Matrix4f scaleMatrix;

    public Scaling(float sx, float sy, float sz) {
        this.scaleMatrix = new Matrix4f(
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0, 0, 1);
    }

    public Scaling() {
        this.scaleMatrix = Matrix4f.identityM();
    }

    @Override
    public Matrix4f getMatrix() {
        return scaleMatrix;
    }

    @Override
    public Vector3f transform(Vector3f v) {
        return new Vector3f(
                scaleMatrix.getElem(0, 0) * v.x,
                scaleMatrix.getElem(1, 1) * v.y,
                scaleMatrix.getElem(2, 2) * v.z);
    }

    public void set(float newSX, float newSY, float newSZ) {
        scaleMatrix.setElem(0, 0, newSX);
        scaleMatrix.setElem(1, 1, newSY);
        scaleMatrix.setElem(2, 2, newSZ);
    }

    public void setRelative(float dTX, float dTY, float dTZ) {
        set(
                scaleMatrix.getElem(0, 0) * dTX,
                scaleMatrix.getElem(1, 1) * dTY,
                scaleMatrix.getElem(2, 2) * dTZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Scaling scaling = (Scaling) o;
        return Objects.equals(scaleMatrix, scaling.scaleMatrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scaleMatrix);
    }
}
