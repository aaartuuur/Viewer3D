package com.cgvsu.render_engine;

public class Parametrs {
    public boolean drawPolygonMash;
    public boolean useTexture;
    public boolean useLighting;

    public float brightnessLamp;


    public Parametrs(boolean drawPolygonMash, boolean useTexture, boolean useLighting, float brightnessLamp) {
        this.drawPolygonMash=drawPolygonMash;
        this.useTexture = useTexture;
        this.useLighting = useLighting;
        this.brightnessLamp = brightnessLamp;
    }
}
