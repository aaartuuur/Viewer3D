package com.cgvsu.affine;

import com.cgvsu.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Transformation implements AffineTransform, DataList<AffineTransform> {
    private final List<AffineTransform> affineTransformations = new ArrayList<>();
    private boolean isCalculated = false;
    private Matrix4f trsMatrix;

    public Transformation(AffineTransform... ats) {
        for (AffineTransform at : ats) {
            affineTransformations.add(at);
        }
    }

    public Transformation() {
    }

    public boolean isCalculated() {
        return isCalculated;
    }


    @Override
    public Matrix4f getMatrix() {
        if (isCalculated()) {
            return trsMatrix;
        }

        trsMatrix = new Matrix4f(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        );

        for (AffineTransform at : affineTransformations) {
            trsMatrix = Matrix4f.multiply(trsMatrix, at.getMatrix());
        }
        isCalculated = true;
        return trsMatrix;
    }

    @Override
    public void add(AffineTransform at) {
        affineTransformations.add(at);
        isCalculated = false;
    }

    @Override
    public void remove(int index) {
        affineTransformations.remove(index);
        isCalculated = false;
    }

    @Override
    public void remove(AffineTransform at) {
        affineTransformations.remove(at);
        isCalculated = false;
    }

    @Override
    public void set(int index, AffineTransform at) {
        affineTransformations.set(index, at);
        isCalculated = false;
    }

    @Override
    public AffineTransform get(int index) {
        return affineTransformations.get(index);
    }
}
