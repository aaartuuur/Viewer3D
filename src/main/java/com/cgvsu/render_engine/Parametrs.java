package com.cgvsu.render_engine;

public class Parametrs {
    public boolean drawPolygonMash;
    public boolean useTexture;
    public boolean useLighting;

    public float brightnessLamp;

    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private float translationX;
    private float translationY;
    private float translationZ;

    // Конструктор
    public Parametrs(boolean drawPolygonMash, boolean useTexture, boolean useLighting, float brightnessLamp,
                     float rotationX, float rotationY, float rotationZ,
                     float scaleX, float scaleY, float scaleZ,
                     float translationX, float translationY, float translationZ) {
        this.drawPolygonMash = drawPolygonMash;
        this.useTexture = useTexture;
        this.useLighting = useLighting;
        this.brightnessLamp = brightnessLamp;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.translationX = translationX;
        this.translationY = translationY;
        this.translationZ = translationZ;
    }

    // Геттеры и сеттеры (опционально)
    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    public float getTranslationZ() {
        return translationZ;
    }

    public void setTranslationZ(float translationZ) {
        this.translationZ = translationZ;
    }
}