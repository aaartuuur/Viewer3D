package com.cgvsu.ation;

import com.cgvsu.GuiController;
import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.Arrays;

public class Rasterization {

    static final Vector3f ray = new Vector3f(1000, 2000, 4000);
    static final double k = 0.6;
    static float[][] holst;

    public Rasterization() {
        Screen screen = Screen.getPrimary();
        int width = (int) screen.getBounds().getWidth();
        int height = (int) screen.getBounds().getHeight();
        holst = new float[height][width];

        for (int i = 0; i < holst.length; i++) {
            Arrays.fill(holst[i], Float.MAX_VALUE);
        }
    }

    public static void fillTriangle(
            final GraphicsContext graphicsContext,
            final int[] arrX,
            final int[] arrY,
            final Color[] colors,
            final ArrayList<Vector3f> normals,
            final float[] vertexsZBuf) {

        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        sort(arrX, arrY, colors);

        for (int y = arrY[1]; y <= arrY[2]; y++) {
            final int x1 = (arrY[2] - arrY[1] == 0) ? arrX[1] :
                    (y - arrY[1]) * (arrX[2] - arrX[1]) / (arrY[2] - arrY[1]) + arrX[1];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                float[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);
                if (zBufer(x, y, vertexsZBuf, barizenticCoordinate)) {
                    pixelWriter.setColor(x, y, getColor(barizenticCoordinate, colors, normals));
                }
            }
        }

        for (int y = arrY[1]; y >= arrY[0]; y--) {
            final int x1 = (arrY[1] - arrY[0] == 0) ? arrX[0] :
                    (y - arrY[0]) * (arrX[1] - arrX[0]) / (arrY[1] - arrY[0]) + arrX[0];
            final int x2 = (arrY[0] - arrY[2] == 0) ? arrX[2] :
                    (y - arrY[2]) * (arrX[0] - arrX[2]) / (arrY[0] - arrY[2]) + arrX[2];
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                float[] barizenticCoordinate = barizentricCalculator(x, y, arrX, arrY);

                if (zBufer(x, y, vertexsZBuf, barizenticCoordinate)) {
                    pixelWriter.setColor(x, y, getColor(barizenticCoordinate, colors, normals));
                }
            }
        }
    }

    private static boolean zBufer(int x, int y, float[] zBuf, float[] barycentricCoords) {
        int xAbs = Math.abs(x);
        int yAbs = Math.abs(y);
        if (xAbs >= holst[0].length || yAbs >= holst.length) {
            return false;
        }
        float len = barycentricCoords[0] * zBuf[0] +
                barycentricCoords[1] * zBuf[1] +
                barycentricCoords[2] * zBuf[2];
        return len < holst[yAbs][xAbs];
    }

    private static float determinator(int[][] arr) {
        return arr[0][0] * arr[1][1] * arr[2][2] + arr[1][0] * arr[0][2] * arr[2][1] +
                arr[0][1] * arr[1][2] * arr[2][0] - arr[0][2] * arr[1][1] * arr[2][0] -
                arr[0][0] * arr[1][2] * arr[2][1] - arr[0][1] * arr[1][0] * arr[2][2];
    }

    private static float[] barizentricCalculator(int x, int y, int[] arrX, int[] arrY) {
        final float generalDeterminant = determinator(new int[][]{arrX, arrY, new int[]{1, 1, 1}});
        final float coordinate0 = Math.abs(determinator(
                new int[][]{new int[]{x, arrX[1], arrX[2]}, new int[]{y, arrY[1], arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final float coordinate1 = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], x, arrX[2]}, new int[]{arrY[0], y, arrY[2]}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        final float coordinate2 = Math.abs(determinator(
                new int[][]{new int[]{arrX[0], arrX[1], x}, new int[]{arrY[0], arrY[1], y}, new int[]{1, 1, 1}}) /
                generalDeterminant);
        return new float[]{coordinate0, coordinate1, coordinate2};
    }

    private static Color getColor(float[] barycentricCoords, Color[] colors, ArrayList<Vector3f> normals) {

        final double red = barycentricCoords[0] * colors[0].getRed() +
                barycentricCoords[1] * colors[1].getRed() +
                barycentricCoords[2] * colors[2].getRed();
        final double green = barycentricCoords[0] * colors[0].getGreen() +
                barycentricCoords[1] * colors[1].getGreen() +
                barycentricCoords[2] * colors[2].getGreen();
        final double blue = barycentricCoords[0] * colors[0].getBlue() +
                barycentricCoords[1] * colors[1].getBlue() +
                barycentricCoords[2] * colors[2].getBlue();
        Vector3f vectorLight = normals.get(0).multiply(barycentricCoords[0]);
        vectorLight.add(normals.get(1).multiply(barycentricCoords[1]));
        vectorLight.add(normals.get(2).multiply(barycentricCoords[2]));
        vectorLight.normalize();
        ray.normalize();
        final float light = -1 * Vector3f.dotProduct(vectorLight, ray);

        return new Color(
                Math.max(0, Math.min(1, red)),
                Math.max(0, Math.min(1, green)),
                Math.max(0, Math.min(1, blue)),
                Math.max(0, Math.min(1, 1 - k + k * (light < 0 ? 0 : light))));
    }

    private static void sort(int[] x, int[] y, Color[] c) {
        if (y[0] > y[1]) {
            swap(x, y, c, 0, 1);
        }
        if (y[1] > y[2]) {
            swap(x, y, c, 1, 2);
        }
        if (y[0] > y[1]) {
            swap(x, y, c, 0, 1);
        }
    }

    private static void swap(int[] x, int[] y, Color[] c, int i, int j) {
        int tempY = y[i];
        int tempX = x[i];
        Color tempC = c[i];
        x[i] = x[j];
        y[i] = y[j];
        c[i] = c[j];
        x[j] = tempX;
        y[j] = tempY;
        c[j] = tempC;
    }
}