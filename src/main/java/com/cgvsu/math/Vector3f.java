package com.cgvsu.math;

import static com.cgvsu.math.Global.EPS;


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

    @Override
    public void sub(Vector3f v) {

    }

    public static Vector3f subtraction(final Vector3f v1, final Vector3f v2) {
        return new Vector3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    @Override
    public final void sub(Vector3f var1, Vector3f var2) {
        this.x = var1.x - var2.x;
        this.y = var1.y - var2.y;
        this.z = var1.z - var2.z;
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

    public static float lenghtTwoVectors(final Vector3f v1, final Vector3f v2){
        return (float) Math.sqrt((v1.x-v2.x) * (v1.x-v2.x) + (v1.y-v2.y) * (v1.y-v2.y) + (v1.z-v2.z) * (v1.z-v2.z));
    }

    @Override
    public Vector3f normal() {
        final float length = this.length();
        if (length < EPS) {
            throw new ArithmeticException("Normalization of a zero vector is not allowed.");
        }
        return this.divide(length);
    }
    public Vector3f negate(){
        return new Vector3f(-x, -y, -z);
    }

    public final void normalize() {
        float var1 = (float)(1.0 / Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z)));
        this.x *= var1;
        this.y *= var1;
        this.z *= var1;
    }

    public static float dotProduct(final Vector3f v1, final Vector3f v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public float dot(final Vector3f v){
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public static Vector3f crossProduct(final Vector3f v1, final Vector3f v2) {
        final float x = v1.y * v2.z - v1.z * v2.y;
        final float y = v1.z * v2.x - v1.x * v2.z;
        final float z = v1.x * v2.y - v1.y * v2.x;
        return new Vector3f(x, y, z);
    }

    public final void cross(Vector3f var1, Vector3f var2) {
        float var3 = var1.y * var2.z - var1.z * var2.y;
        float var4 = var2.x * var1.z - var2.z * var1.x;
        this.z = var1.x * var2.y - var1.y * var2.x;
        this.x = var3;
        this.y = var4;
    }

    public Vector3f clone(){
        return new Vector3f(this.x, this.y, this.z);
    }

    @Override
    public boolean equals(final Vector3f other) {
        return Math.abs(x - other.x) < EPS
                && Math.abs(y - other.y) < EPS
                && Math.abs(z - other.z) < EPS;
    }

    public float get(int ind){
        if(ind == 0){return x;}
        if(ind == 1){return y;}
        if(ind == 2){return z;}
        return 0;
    }

    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setZ(float z){
        this.z = z;
    }
}
