package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

import javax.vecmath.Point2f;

public class GraphicConveyor {


    public static Matrix4f rotateScaleTranslate(Parametrs parametrs) {
        // Извлекаем параметры
        float rotationX = (float) Math.toRadians(parametrs.getRotationX()); // Поворот вокруг X (в радианах)
        float rotationY = (float) Math.toRadians(parametrs.getRotationY()); // Поворот вокруг Y (в радианах)
        float rotationZ = (float) Math.toRadians(parametrs.getRotationZ()); // Поворот вокруг Z (в радианах)
        float scaleX = parametrs.getScaleX(); // Масштабирование по X
        float scaleY = parametrs.getScaleY(); // Масштабирование по Y
        float scaleZ = parametrs.getScaleZ(); // Масштабирование по Z
        float translationX = parametrs.getTranslationX(); // Перемещение по X
        float translationY = parametrs.getTranslationY(); // Перемещение по Y
        float translationZ = parametrs.getTranslationZ(); // Перемещение по Z

        float cosX = (float) Math.cos(rotationX);
        float sinX = (float) Math.sin(rotationX);
        float[] rotateX = new float[]{
                1, 0, 0, 0,
                0, cosX, -sinX, 0,
                0, sinX, cosX, 0,
                0, 0, 0, 1
        };

        float cosY = (float) Math.cos(rotationY);
        float sinY = (float) Math.sin(rotationY);
        float[] rotateY = new float[]{
                cosY, 0, sinY, 0,
                0, 1, 0, 0,
                -sinY, 0, cosY, 0,
                0, 0, 0, 1
        };

        float cosZ = (float) Math.cos(rotationZ);
        float sinZ = (float) Math.sin(rotationZ);
        float[] rotateZ = new float[]{
                cosZ, -sinZ, 0, 0,
                sinZ, cosZ, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };

        float[] scale = new float[]{
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        };

        float[] translate = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                translationX, translationY, translationZ, 1
        };

        float[] result = multiplyMatrices(multiplyMatrices(multiplyMatrices(rotateZ, multiplyMatrices(rotateY, rotateX)), scale), translate);

        return new Matrix4f(result);
    }

    // Метод для умножения двух матриц 4x4
    private static float[] multiplyMatrices(float[] a, float[] b) {
        float[] result = new float[16];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i * 4 + j] += a[i * 4 + k] * b[k * 4 + j];
                }
            }
        }
        return result;
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();



        resultZ.sub(target, eye);
        resultX.cross(up, resultZ);
        resultY.cross(resultZ, resultX);

        resultX.normalize();
        resultY.normalize();
        resultZ.normalize();


        float[] matrix = new float[]{
                resultX.x, resultY.x, resultZ.x, 0,
                resultX.y, resultY.y, resultZ.y, 0,
                resultX.z, resultY.z, resultZ.z, 0,
                -resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1};
        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        float[][] result = new float[4][4];
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));

        result[0][0] = tangentMinusOnDegree / aspectRatio;
        result[1][1] = tangentMinusOnDegree;
        result[2][2] = (farPlane + nearPlane) / (farPlane - nearPlane);
        result[2][3] = 1.0F;
        result[3][2] = 2 * (nearPlane * farPlane) / (nearPlane - farPlane);
        return new Matrix4f(result);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        final float x = (vertex.x * matrix.getElem(0,0)) + (vertex.y * matrix.getElem(1,0)) +
                (vertex.z * matrix.getElem(2,0)) + matrix.getElem(3,0);
        final float y = (vertex.x * matrix.getElem(0,1)) + (vertex.y * matrix.getElem(1,1)) +
                (vertex.z * matrix.getElem(2,1)) + matrix.getElem(3,1);
        final float z = (vertex.x * matrix.getElem(0,2)) + (vertex.y * matrix.getElem(1,2)) +
                (vertex.z * matrix.getElem(2,2)) + matrix.getElem(3,2);
        final float w = (vertex.x * matrix.getElem(0,3)) + (vertex.y * matrix.getElem(1,3)) +
                (vertex.z * matrix.getElem(2,3)) + matrix.getElem(3,3);
        return new Vector3f(x / w, y / w, z / w);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }

}
