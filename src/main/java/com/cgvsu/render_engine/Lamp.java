package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;

import java.awt.*;

public class Lamp {
    public Vector3f coordinates;
    public Color color;
    public Lamp(Vector3f coordinates, Color color){
        this.color = color;
        this.coordinates = coordinates;
    }
    public static Color convert(javafx.scene.paint.Color color) {
        return new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
    }
}
