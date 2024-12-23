package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;

import static com.cgvsu.render_engine.GraphicConveyor.multiplyMatrix4ByVector3;

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

    public void rotateCam(double x, double y, boolean isPrimaryButtonDown){
        if(isPrimaryButtonDown) {
            double deltaX =  (x - mousePositionX);
            double deltaY =  (y - mousePositionY);

            double rotX =  (-deltaY * 0.2);
            double rotY =  (-deltaX * 0.2);

            Matrix4f rotMatrX = Matrix4f.rotate((float) rotX, 1, 0, 0);
            Matrix4f rotMatrY = Matrix4f.rotate((float) rotY, 0, 1, 0);

            Matrix4f rotMatr = Matrix4f.multiply(rotMatrX, rotMatrY);

            position = multiplyMatrix4ByVector3(rotMatr, position);
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