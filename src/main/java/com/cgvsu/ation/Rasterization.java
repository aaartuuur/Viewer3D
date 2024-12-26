package com.cgvsu.ation;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.render_engine.Lamp;
import com.cgvsu.render_engine.Parametrs;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.Arrays;
import java.util.List;

public class Rasterization {

    static List<Lamp> lightsVectors;
    static Vector3f ray;
    static final double k = 0.6;
    static float[][] holst;//zbuf

    public Rasterization(Vector3f camera, List<Lamp> lights) {
        Screen screen = Screen.getPrimary();
        int width = (int) screen.getBounds().getWidth();
        int height = (int) screen.getBounds().getHeight();
        holst = new float[height][width];
        ray = camera.clone();
        lightsVectors = lights;
        for (int i = 0; i < holst.length; i++) {
            Arrays.fill(holst[i], Float.MAX_VALUE);
        }
    }

    public static void drawLine(
            final GraphicsContext graphicsContext,
            int x1, int y1,
            int x2, int y2,
            float z1, float z2,
            final Color color) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        if (y1 > y2) {
            int tempY = y1;
            y1 = y2;
            y2 = tempY;
            int tempX = x1;
            x1 = x2;
            x2 = tempX;
            float tempZ = z1;
            z1 = z2;
            z2 = tempZ;
        }

        int dx = x2 - x1;
        int dy = y2 - y1;
        float dz = z2 - z1;

        int x = x1;
        int y = y1;
        float z = z1;

        int stepX = (dx > 0) ? 1 : -1;
        int stepY = (dy > 0) ? 1 : -1;

        dx = Math.abs(dx);
        dy = Math.abs(dy);

        int ddx, ddy;
        if (dx > dy) {
            ddx = stepX;
            ddy = stepY;
        } else {
            ddx = stepX;
            ddy = stepY;
        }

        int error = (dx > dy ? dx : -dy) / 2;
        int error2;

        for (int i = 0; i <= (Math.max(dx, dy)); i++) {
            float zStep = dz / (Math.max(dx, dy));
            if (zBufferForLine(x, y, z)) {
                pixelWriter.setColor(x, y, color);
            }
            z += zStep;

            error2 = error;
            if (error2 > -dx) {
                error -= dy;
                x += ddx;
            }
            if (error2 < dy) {
                error += dx;
                y += ddy;
            }
        }
    }

    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            int[] arrX,
            int[] arrY,
            Color[] colors,
            Vector3f[] normals,
            float[] vertexsZBuf,
            Image textureImage,
            Vector2f[] textureCoords,
            Parametrs parametrs) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final PixelReader pixelReader = textureImage == null ? null : textureImage.getPixelReader();

        sort(arrX, arrY, vertexsZBuf, colors, normals, textureCoords);

        for (int y = arrY[1]; y <= arrY[2]; y++) {
            final int x1 = (arrY[2] - arrY[1] == 0) ? arrX[1] :
                    (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                float[] barizenticCoordinate = barycentricCalculator(x, y, arrX, arrY);
                if (zBufer(x, y, vertexsZBuf, barizenticCoordinate)) {
                    if (parametrs.useTexture){
                        float u = barizenticCoordinate[0] * textureCoords[0].x +
                                barizenticCoordinate[1] * textureCoords[1].x +
                                barizenticCoordinate[2] * textureCoords[2].x;
                        float v = barizenticCoordinate[0] * textureCoords[0].y +
                                barizenticCoordinate[1] * textureCoords[1].y +
                                barizenticCoordinate[2] * textureCoords[2].y;


                        int xt = (int) Math.floor((1-u) * textureImage.getWidth());
                        int yt = (int) Math.floor((1-v) * textureImage.getHeight());

                        xt = (int) Math.max(0, Math.min(textureImage.getWidth() - 1, xt));
                        yt = (int) Math.max(0, Math.min(textureImage.getHeight() - 1, yt));

                        Color textureColor = pixelReader.getColor(xt, yt);
                        pixelWriter.setColor(x, y, getColor(barizenticCoordinate, textureColor, normals, parametrs.brightnessLamp));
                    }else {
                        pixelWriter.setColor(x, y, getColor(barizenticCoordinate, colors, normals, parametrs.brightnessLamp));
                    }
                }
            }
        }

        for (int y = arrY[1]; y >= arrY[0]; y--) {
            final int x1 = (arrY[1] - arrY[0] == 0) ? arrX[0] :
                    (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                float[] barizenticCoordinate = barycentricCalculator(x, y, arrX, arrY);
                if (zBufer(x, y, vertexsZBuf, barizenticCoordinate)) {
                    if (parametrs.useTexture){
                        float u = barizenticCoordinate[0] * textureCoords[0].x +
                                barizenticCoordinate[1] * textureCoords[1].x +
                                barizenticCoordinate[2] * textureCoords[2].x;
                        float v = barizenticCoordinate[0] * textureCoords[0].y +
                                barizenticCoordinate[1] * textureCoords[1].y +
                                barizenticCoordinate[2] * textureCoords[2].y;

                        int xt = (int) Math.floor((1-u) * textureImage.getWidth());
                        int yt = (int) Math.floor((1-v) * textureImage.getHeight());

                        xt = (int) Math.max(0, Math.min(textureImage.getWidth() - 1, xt));
                        yt = (int) Math.max(0, Math.min(textureImage.getHeight() - 1, yt));

                        Color textureColor = pixelReader.getColor(xt, yt);
                        pixelWriter.setColor(x, y, getColor(barizenticCoordinate, textureColor, normals, parametrs.brightnessLamp));
                    }else {
                        pixelWriter.setColor(x, y, getColor(barizenticCoordinate, colors, normals, parametrs.brightnessLamp));
                    }
                }
            }
        }
    }

    private static boolean zBufer(int x, int y, float[] zBuf, float[] barycentricCoords) {
        if (x >= holst[0].length || y >= holst.length || x < 0 || y < 0) {
            return false;
        }
        float len = barycentricCoords[0] * zBuf[0] +
                barycentricCoords[1] * zBuf[1] +
                barycentricCoords[2] * zBuf[2];
        if (holst[y][x]>len) {
            holst[y][x] = len;
            return true;
        }
        return false;
    }
    private static boolean zBufferForLine(int x, int y, float z){
        if (x >= holst[0].length || y >= holst.length || x < 0 || y < 0) {
            return false;
        }
        if (holst[y][x]>z) {
            if (z<0.9997919){
                holst[y][x] = z-0.000005F;;
                return true;
            }else if (z>0.9999999){
                holst[y][x] = z - 0.0000006F;
                return true;
            }
            holst[y][x] = z - 0.000002F;
            return true;
        }
        return false;
    }

    private static float determinator(int[][] arr) {
        return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] +
                arr[0][1] * arr[1][2] * arr[2][0] - arr[0][2] * arr[1][1] * arr[2][0] -
                arr[0][0] * arr[1][2] * arr[2][1] - arr[0][1] * arr[1][0] * arr[2][2];
    }

    private static float[] barycentricCalculator(int x, int y, int[] arrX, int[] arrY) {
        float v0x = arrX[2] - arrX[0];
        float v0y = arrY[2] - arrY[0];
        float v1x = arrX[1] - arrX[0];
        float v1y = arrY[1] - arrY[0];
        float v2x = x - arrX[0];
        float v2y = y - arrY[0];

        // Вычисление скалярных произведений
        float dot00 = v0x * v0x + v0y * v0y;
        float dot01 = v0x * v1x + v0y * v1y;
        float dot02 = v0x * v2x + v0y * v2y;
        float dot11 = v1x * v1x + v1y * v1y;
        float dot12 = v1x * v2x + v1y * v2y;

        // Вычисление барицентрических координат
        float invDenominator = 1.0f / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenominator;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenominator;
        float w = 1.0f - u - v;

        return new float[]{w, v, u};
    }

    private static Color getColor(float[] barycentricCoords, Color[] colors, Vector3f[] normals, float brightness) {
        Vector3f vectorLight = normals[0].multiply(barycentricCoords[0]);
        vectorLight.add(normals[1].multiply(barycentricCoords[1]));
        vectorLight.add(normals[2].multiply(barycentricCoords[2]));
        vectorLight.normalize();
        final float l = -1 * Vector3f.dotProduct(vectorLight, ray.equals(new Vector3f()) ? new Vector3f(): ray.normal());

        final double red = barycentricCoords[0] * colors[0].getRed() +
                barycentricCoords[1] * colors[1].getRed() +
                barycentricCoords[2] * colors[2].getRed();
        final double green = barycentricCoords[0] * colors[0].getGreen() +
                barycentricCoords[1] * colors[1].getGreen() +
                barycentricCoords[2] * colors[2].getGreen();
        final double blue = barycentricCoords[0] * colors[0].getBlue() +
                barycentricCoords[1] * colors[1].getBlue() +
                barycentricCoords[2] * colors[2].getBlue();

        double redLamp = 0;
        double greenLamp = 0;
        double blueLamp = 0;
        for (Lamp lamp : lightsVectors) {
            Vector3f lightDirection = Vector3f.subtraction(lamp.coordinates, new Vector3f(0, 0, 0)); // поменять на координаты обЪекта


            float lightIntensity = -1 * Vector3f.dotProduct(vectorLight, lightDirection.equals(new Vector3f()) ? new Vector3f(): lightDirection.normal()); // Интенсивность света

            redLamp = Math.max(lamp.color.getRed() * lightIntensity, redLamp);
            greenLamp = Math.max(lamp.color.getGreen() * lightIntensity, greenLamp);
            blueLamp = Math.max(lamp.color.getBlue() * lightIntensity, blueLamp);
        }
        return new Color(
                Math.max(0, Math.min(1, red * (1 - k) + red * k * l + brightness*k*redLamp)),
                Math.max(0, Math.min(1, green * (1 - k) + green * k * l + brightness*k*greenLamp)),
                Math.max(0, Math.min(1, blue * (1 - k) + blue * k * l + brightness*k*blueLamp)),
                1);
    }

    private static Color getColor(float[] barycentricCoords, Color color, Vector3f[] normals, float brightness) {
        Vector3f vectorLight = normals[0].multiply(barycentricCoords[0]);
        vectorLight.add(normals[1].multiply(barycentricCoords[1]));
        vectorLight.add(normals[2].multiply(barycentricCoords[2]));
        vectorLight.normalize();
        final float l = -1 * Vector3f.dotProduct(vectorLight, ray.equals(new Vector3f()) ? new Vector3f(): ray.normal());

        double redLamp = 0;
        double greenLamp = 0;
        double blueLamp = 0;
        for (Lamp lamp : lightsVectors) {
            Vector3f lightDirection = Vector3f.subtraction(lamp.coordinates, new Vector3f(0, 0, 0)); // поменять на координаты обЪекта


            float lightIntensity = -1 * Vector3f.dotProduct(vectorLight, lightDirection.equals(new Vector3f()) ? new Vector3f(): lightDirection.normal()); // Интенсивность света

            redLamp = Math.max(lamp.color.getRed() * lightIntensity, redLamp);
            greenLamp = Math.max(lamp.color.getGreen() * lightIntensity, greenLamp);
            blueLamp = Math.max(lamp.color.getBlue() * lightIntensity, blueLamp);
        }
        return new Color(
                Math.max(0, Math.min(1, color.getRed() * (1 - k) + color.getRed() * k * l + brightness*k*redLamp)),
                Math.max(0, Math.min(1, color.getGreen() * (1 - k) + color.getGreen() * k * l + brightness*k*greenLamp)),
                Math.max(0, Math.min(1, color.getBlue() * (1 - k) + color.getBlue() * k * l + brightness*k*blueLamp)),
                1);
    }

    private static void sort(int[] x, int[] y, float[] z, Color[] c, Vector3f[] n, Vector2f[] tc) {
        if (y[0] > y[1]) {
            swap(x, y, c, z, n, tc, 0, 1);
        }
        if (y[1] > y[2]) {
            swap(x, y, c, z, n, tc, 1, 2);
        }
        if (y[0] > y[1]) {
            swap(x, y, c, z, n, tc,0, 1);
        }
    }

    private static void swap(int[] x, int[] y, Color[] c, float[] z, Vector3f[] n, Vector2f[] tc, int i, int j) {
        int tempY = y[i];
        int tempX = x[i];
        Color tempC = c[i];
        float tempZ = z[i];
        Vector3f tempN = n[i];
        Vector2f tempTc = tc[i];

        x[i] = x[j];
        y[i] = y[j];
        c[i] = c[j];
        z[i] = z[j];
        n[i] = n[j];
        tc[i] = tc[j];

        x[j] = tempX;
        y[j] = tempY;
        c[j] = tempC;
        z[j] = tempZ;
        n[j] = tempN;
        tc[j] = tempTc;
    }
}