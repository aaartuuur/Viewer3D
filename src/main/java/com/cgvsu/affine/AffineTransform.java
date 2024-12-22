package com.cgvsu.affine;

import com.cgvsu.math.Matrix4x4;
import com.cgvsu.math.Vector3f;


public class AffineTransform {

    private Order rotOrder = Order.XYZ;

    private Vector3f translation = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(1, 1, 1);
    private Vector3f scale = new Vector3f(1, 1, 1);

    private Matrix4x4 rotationMatrix = new Matrix4x4(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);
    private Matrix4x4 scaleMatrix;
    private Matrix4x4 translationMatrix;
    private Matrix4x4 affineMatrix = new Matrix4x4(1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);

    public AffineTransform() {
    }

}

