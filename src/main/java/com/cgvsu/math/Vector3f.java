package com.cgvsu.math;

import static com.cgvsu.math.Global.EPS;

;

public class Vector3f implements Vector<Vector3f> {
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public float x, y, z;

    public static Vector3f addition(final Vector3f v1, final Vector3f v2) {
        return new Vector3f(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    @Override
    public void add(final Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public static Vector3f subtraction(final Vector3f v1, final Vector3f v2) {
        return new Vector3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    @Override
    public void sub(final Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
    }

    @Override
    public Vector3f multiply(float c) {
        return new Vector3f(c * x, c * y, c * z);
    }

    @Override
    public void mult(float c) {
        x *= c;
        y *= c;
        z *= c;
    }

    @Override
    public Vector3f divide(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return new Vector3f(x / c, y / c, z / c);
    }

    @Override
    public void div(float c) {
        if (c < EPS) {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        x /= c;
        y /= c;
        z /= c;
    }

    @Override
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public Vector3f normal() {
        final float length = this.length();
        if (length < EPS) {
            throw new ArithmeticException("Normalization of a zero vector is not allowed.");
        }
        return this.divide(length);
    }

    public static float dotProduct(final Vector3f v1, final Vector3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Vector3f crossProduct(final Vector3f v1, final Vector3f v2) {
        final float x = v1.y * v2.z - v1.z * v2.y;
        final float y = v1.z * v2.x - v1.x * v2.z;
        final float z = v1.x * v2.y - v1.y * v2.x;
        return new Vector3f(x, y, z);
    }// 0 сонапрв

    @Override
    public boolean equals(final Vector3f other) {
        return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS
                && Math.abs(z - other.z) < EPS;
    }
}
