package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

public class Camera {

    private Vector3f position;
    private Vector3f target;
    private float fov;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
    private double mousePositionX;
    private double mousePositionY;
    private String name = "Camera 1";

    private float horizontalAng;   // Угол вращения вокруг объекта (в горизонтальной плоскости)
    private float verticalAng;

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
        this.mousePositionX = mousePositionX;
        this.mousePositionY = mousePositionY;
        this.horizontalAng = 0;
        this.verticalAng = 0;
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

public void rotateCam(double x, double y, boolean isPrimaryButtonDown) {
    if (isPrimaryButtonDown) {
        double deltaX = x - mousePositionX;
        double deltaY = -(y - mousePositionY);

        horizontalAng += deltaX * 0.2;
        verticalAng += deltaY * 0.2;

        if (verticalAng > 89.9F) {
            verticalAng = 89.9F;
        } else if (verticalAng < -89.9F) {
            verticalAng = -89.9F;
        }

        float radius = Vector3f.lenghtTwoVectors(target, position);

        //формулы сферических координат
        float xCam = target.x + radius * (float) Math.cos(Math.toRadians(verticalAng)) * (float) Math.sin(Math.toRadians(horizontalAng));
        float yCam = target.y + radius * (float) Math.sin(Math.toRadians(verticalAng));
        float zCam = target.z + radius * (float) Math.cos(Math.toRadians(verticalAng)) * (float) Math.cos(Math.toRadians(horizontalAng));

        position.set(xCam, yCam, zCam);
    }

    mousePositionX = x;
    mousePositionY = y;
}
    public String getName() {
        return name;
    }

    // Сеттер для имени камеры
    public void setName(String name) {
        this.name = name;
    }

    Matrix4f getViewMatrix() {
        return GraphicConveyor.lookAt(position, target);
    }

    Matrix4f getProjectionMatrix() {
        return GraphicConveyor.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    @Override
    public Camera clone() {
        try {
            Camera clonedCamera = (Camera) super.clone();

            clonedCamera.position = this.position.clone();
            clonedCamera.target = this.target.clone();

            return clonedCamera;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Ошибка клонирования объекта Camera", e);
        }
    }

}