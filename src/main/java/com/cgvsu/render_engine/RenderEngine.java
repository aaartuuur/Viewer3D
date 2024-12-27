package com.cgvsu.render_engine;

import com.cgvsu.ation.FindNormals;
import com.cgvsu.ation.Rasterization;
import com.cgvsu.ation.Triangulation;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import javax.vecmath.Point2f;
import java.util.ArrayList;
import java.util.List;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            Parametrs parametrs,
            Image textureImage,
            List<Lamp> lights) {
        new Rasterization(!parametrs.useLighting ? new Vector3f() : Vector3f.subtraction(camera.getTarget(), camera.getPosition()), lights);//static
        Matrix4f modelMatrix = rotateScaleTranslate(parametrs);
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix = Matrix4f.multiply(modelViewProjectionMatrix, viewMatrix);
        modelViewProjectionMatrix = Matrix4f.multiply(modelViewProjectionMatrix, projectionMatrix);

        mesh.normals = FindNormals.findNormals(mesh);
        Vector3f vertexCoordinate = new Vector3f(parametrs.getTranslationX(),
                parametrs.getTranslationY(), parametrs.getTranslationZ());
        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
            ArrayList<Point2f> resultPoints = new ArrayList<>();
            float[] zVertexs = new float[nVerticesInPolygon];
            int[] numVertexs = new int[nVerticesInPolygon];
            Vector2f[] textureCoords = new Vector2f[nVerticesInPolygon];
            Vector3f[] vertices = new Vector3f[nVerticesInPolygon];
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int tek = mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd);
                numVertexs[vertexInPolygonInd] = tek;
                vertices[vertexInPolygonInd] = mesh.vertices.get(tek); // Сохраняем координаты вершины
            }

            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertexVecmath = new Vector3f(vertices[vertexInPolygonInd].x, vertices[vertexInPolygonInd].y, vertices[vertexInPolygonInd].z);
                Vector3f Vecmath = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                Point2f resultPoint = vertexToPoint(Vecmath, width, height);
                zVertexs[vertexInPolygonInd] = Vecmath.z;
                resultPoints.add(resultPoint);
                textureCoords[vertexInPolygonInd] = mesh.textureVertices.get(
                        mesh.polygons.get(polygonInd).getTextureVertexIndices().get(vertexInPolygonInd));
            }

            if(parametrs.drawPolygonMash) {
                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                    Rasterization.drawLine(
                            graphicsContext,
                            (int) resultPoints.get(vertexInPolygonInd - 1).x,
                            (int) resultPoints.get(vertexInPolygonInd - 1).y,
                            (int) resultPoints.get(vertexInPolygonInd).x,
                            (int) resultPoints.get(vertexInPolygonInd).y,
                            zVertexs[vertexInPolygonInd - 1], zVertexs[vertexInPolygonInd],
                            Color.BLACK);
                }
                Rasterization.drawLine(
                        graphicsContext,
                        (int) resultPoints.get(nVerticesInPolygon - 1).x,
                        (int) resultPoints.get(nVerticesInPolygon - 1).y,
                        (int) resultPoints.get(0).x,
                        (int) resultPoints.get(0).y,
                        zVertexs[nVerticesInPolygon - 1], zVertexs[0],
                        Color.BLACK);
            }
            if (nVerticesInPolygon > 3) {
                List<int[]> triangles = Triangulation.earClippingTriangulate(resultPoints);

                for (int[] triangl : triangles) {
                    Vector3f[] normals = new Vector3f[3];
                    normals[0] = (mesh.normals.get(numVertexs[triangl[0]]));
                    normals[1] = (mesh.normals.get(numVertexs[triangl[1]]));
                    normals[2] = (mesh.normals.get(numVertexs[triangl[2]]));
                    Rasterization.fillTriangle(graphicsContext,
                            new int[]{(int) resultPoints.get(triangl[0]).x, (int) resultPoints.get(triangl[1]).x, (int) resultPoints.get(triangl[2]).x},
                            new int[]{(int) resultPoints.get(triangl[0]).y, (int) resultPoints.get(triangl[1]).y, (int) resultPoints.get(triangl[2]).y},
                            new Color[]{Color.BLUE, Color.BLUE, Color.BLUE},
                            normals,
                            new float[]{zVertexs[triangl[0]], zVertexs[triangl[1]], zVertexs[triangl[2]]},
                            textureImage,
                            new Vector2f[]{textureCoords[triangl[0]], textureCoords[triangl[1]], textureCoords[triangl[2]]},
                            parametrs, vertexCoordinate);
                }
            } else {
                Vector3f[] normals = new Vector3f[3];
                normals[0] = (mesh.normals.get(0));
                normals[1] = (mesh.normals.get(1));
                normals[2] = (mesh.normals.get(2));

                Vector3f[] vertexCoordinates = new Vector3f[3];
                vertexCoordinates[0] = mesh.vertices.get(0);
                vertexCoordinates[1] = mesh.vertices.get(1);
                vertexCoordinates[2] = mesh.vertices.get(2);
                Rasterization.fillTriangle(graphicsContext,
                        new int[]{(int) resultPoints.get(0).x, (int) resultPoints.get(1).x, (int) resultPoints.get(2).x},
                        new int[]{(int) resultPoints.get(0).y, (int) resultPoints.get(1).y, (int) resultPoints.get(2).y},
                        new Color[]{Color.BLUE, Color.BLUE, Color.BLUE},
                        normals,
                        new float[]{zVertexs[0], zVertexs[1], zVertexs[2]},
                        textureImage,
                        new Vector2f[]{textureCoords[0], textureCoords[1], textureCoords[2]},
                        parametrs, vertexCoordinate);
            }

        }
    }
}