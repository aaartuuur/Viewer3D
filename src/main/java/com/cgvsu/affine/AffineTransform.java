package com.cgvsu.affine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.GraphicConveyor;

import java.util.ArrayList;
import java.util.List;

public interface AffineTransform {
    Matrix4f getMatrix();

    default Vector3f transform(Vector3f v) {
        Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(getMatrix(), v);
        return result;
    }

    default List<Vector3f> transform(List<Vector3f> v) {
        Matrix4f m = getMatrix();
        List<Vector3f> resV = new ArrayList<>();

        for (int i = 0; i < v.size(); i++) {
            Vector3f result = GraphicConveyor.multiplyMatrix4ByVector3(m, v.get(i));
            resV.add(result);
        }

        return resV;
    }
}