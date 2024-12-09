package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;

import javax.vecmath.Matrix4f;

public class Camera {

    public Camera(
            final Vector3f position,
            final Vector3f target,
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        this.position = position;
        this.target = target;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    public void setPosition(final Vector3f position) {
        this.position = position;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }

    public void setAspectRatio(final float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void movePosition(final Vector3f translation) {
        this.position.add(translation);
    }

    public void moveTarget(final Vector3f translation) {
        this.target.add(translation);
    }

    public void rotate(float angleX, float angleY) {
        // Вычисляем вектор направления камеры
        // Вычисляем вектор направления камеры
        Vector3f direction = Vector3f.subtraction(target, position);

        // Преобразуем углы в радианы
        float radX = (float) Math.toRadians(angleX);
        float radY = (float) Math.toRadians(angleY);

        // Вычисляем новые координаты цели с использованием тригонометрических функций
        float newX = (float) (direction.x * Math.cos(radY) - direction.z * Math.sin(radY));
        float newZ = (float) (direction.x * Math.sin(radY) + direction.z * Math.cos(radY));

        float newY = (float) (direction.y * Math.cos(radX) - newZ * Math.sin(radX));
        newZ = (float) (direction.y * Math.sin(radX) + newZ * Math.cos(radX));

        // Обновляем цель камеры
        target = Vector3f.addition(position, new Vector3f(newX, newY, newZ));
    }

    Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
}