package com.cgvsu.math;

import static com.cgvsu.math.Global.EPS;

public class Matrix4x4 {

    private float[][] mat;

    public Matrix4x4(float[][] mat){
        if (mat.length != SIZE || mat[0].length != SIZE) {
            throw new IllegalArgumentException("Matrix must be 4x4");
        }
        this.mat = mat;
    }

    public Matrix4x4() {
        this.mat = new float[SIZE][SIZE];
    }
    public Matrix4x4(float[] num) {
        this.mat = new float[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.mat[i][j] = num[i * SIZE + j];
            }
        }
    }

    public Matrix4x4(
            float m00, float m01, float m02, float m03,
            float m10, float m11, float m12, float m13,
            float m20, float m21, float m22, float m23,
            float m30, float m31, float m32, float m33) {
        mat = new float[][]{
                {m00, m01, m02, m03},
                {m10, m11, m12, m13},
                {m20, m21, m22, m23},
                {m30, m31, m32, m33}
        };
    }

    static final private int SIZE = 4;

    public Matrix4x4(Matrix4x4 t) {
        float [][] m = t.toArray();
        this.mat = m;
    }

    public Matrix4x4(double[][] rotationMatrix) {
    }

    public float[][] toArray() {
        return mat;
    }

    public static Matrix4x4 add(final Matrix4x4 m1, final Matrix4x4 m2){
        Matrix4x4 res = new Matrix4x4(new float[SIZE][SIZE]);
        for(int row = 0; row<SIZE; row++){
            for(int col = 0; col<SIZE; col++){
                res.mat[row][col] = m1.mat[row][col] + m2.mat[row][col];
            }
        }
        return res;
    }

    public static Matrix4x4 sub(final Matrix4x4 m1, final Matrix4x4 m2){
        Matrix4x4 res = new Matrix4x4(new float[SIZE][SIZE]);
        for(int row = 0; row<SIZE; row++){
            for(int col = 0; col<SIZE; col++){
                res.mat[row][col] = m1.mat[row][col] - m2.mat[row][col];
            }
        }
        return res;
    }


    public static Matrix4x4 multiply(final Matrix4x4 m1, final Matrix4x4 m2){
        Matrix4x4 res = new Matrix4x4(new float[SIZE][SIZE]);;
        for (int m1row = 0; m1row<SIZE; m1row++){
            for (int m2col = 0; m2col<SIZE; m2col++){
                float a = 0;
                for (int i = 0; i<SIZE; i++){
                    a+=m1.mat[m1row][i] * m2.mat[i][m2col];
                }
                res.mat[m1row][m2col] = a;
            }
        }
        return res;
    }

    public Vector4f mulVector(final Vector4f v){
        float[] arr = new float[]{v.x, v.y, v.z, v.w};
        float[] res = new float[SIZE];
        for (int row = 0; row < SIZE; row++){
            for (int col = 0; col<SIZE; col++){
                res[row] += this.mat[row][col]*arr[col];
            }
        }
        return new Vector4f(res[0], res[1], res[2], res[3]);
    }

    public Matrix4x4 multiply(float scalar) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.mat[i][j] * scalar;
            }
        }
        return new Matrix4x4(result);
    }

    public void transpose(){
        for(int row = 0; row<SIZE; row++) {
            for (int col = row + 1; col < SIZE; col++) {
                float a = this.mat[row][col];//swap
                this.mat[row][col] = this.mat[col][row];
                this.mat[col][row] = a;
            }
        }
    }

    public float determinant() {
        float det = 0;
        for (int i = 0; i < SIZE; i++) {
            float[][] minor = getMinor(0, i);
            Matrix3f minorMatrix = new Matrix3f(minor);
            det += (float) (Math.pow(-1, i) * mat[0][i] * minorMatrix.determinant());
        }
        return det;
    }

    private float[][] getMinor(int row, int col) {
        float[][] minor = new float[3][3];
        int minorRow = 0;
        for (int i = 0; i < SIZE; i++) {
            if (i == row) continue;
            int minorCol = 0;
            for (int j = 0; j < SIZE; j++) {
                if (j == col) continue;
                minor[minorRow][minorCol] = mat[i][j];
                minorCol++;
            }
            minorRow++;
        }
        return minor;
    }

    public boolean equals(final Matrix4x4 other){
        for(int row = 0; row<SIZE; row++){
            for(int col = 0; col<SIZE; col++){
                if (Math.abs(this.mat[row][col] - other.mat[row][col]) >= EPS){
                    return false;
                }
            }
        }
        return true;
    }


    public static Matrix4x4 rotate(float angle, float axisX, float axisY, float axisZ) {
        double radians = (double) Math.toRadians(angle);
        double sin = (double) Math.sin(radians);
        double cos = (double) Math.cos(radians);
        double oneMinusCos = 1.0 - cos;

        float[][] rotationMatrix = {
                {(float) (cos + axisX * axisX * oneMinusCos), (float) (axisX * axisY * oneMinusCos - axisZ * sin), (float) (axisX * axisZ * oneMinusCos + axisY * sin), 0},
                {(float) (axisY * axisX * oneMinusCos + axisZ * sin), (float) (cos + axisY * axisY * oneMinusCos), (float) (axisY * axisZ * oneMinusCos - axisX * sin), 0},
                {(float) (axisZ * axisX * oneMinusCos - axisY * sin), (float) (axisZ * axisY * oneMinusCos + axisX * sin), (float) (cos + axisZ * axisZ * oneMinusCos), 0},
                {0, 0, 0, 1}
        };

        return new Matrix4x4(rotationMatrix);
    }

    public float getElem(int x, int y){
        return mat[x][y];
    }

    public void setElem(int x, int y, float value){
        mat[x][y] = value;
    }


}
