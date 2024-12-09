package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.ation.FindNormals;
import com.cgvsu.ation.Rasterization;
import com.cgvsu.ation.Triangulation;
import com.cgvsu.math.Vector3f;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;

import static com.cgvsu.render_engine.GraphicConveyor.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height) {
        new Rasterization(Vector3f.subtraction(camera.getTarget(), camera.getPosition()));
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        mesh.normals = FindNormals.findNormals(mesh);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
            ArrayList<Point2f> resultPoints = new ArrayList<>();
            float[] zVertexs = new float[nVerticesInPolygon];
            int[] numVertexs = new int[nVerticesInPolygon];
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int tek = mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd);
                numVertexs[vertexInPolygonInd] = tek;
                Vector3f vertex = mesh.vertices.get(tek);
                Vector3f vertexVecmath = new Vector3f(vertex.x, vertex.y, vertex.z);
                Vector3f Vecmath = multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath);
                Point2f resultPoint = vertexToPoint(Vecmath, width, height);
                zVertexs[vertexInPolygonInd] = Vecmath.z;
                resultPoints.add(resultPoint);
            }

//            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
//                graphicsContext.strokeLine(
//                        resultPoints.get(vertexInPolygonInd - 1).x,
//                        resultPoints.get(vertexInPolygonInd - 1).y,
//                        resultPoints.get(vertexInPolygonInd).x,
//                        resultPoints.get(vertexInPolygonInd).y);
//            }
//
//            graphicsContext.strokeLine(
//                    resultPoints.get(nVerticesInPolygon - 1).x,
//                    resultPoints.get(nVerticesInPolygon - 1).y,
//                    resultPoints.get(0).x,
//                    resultPoints.get(0).y);

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
                            new float[]{zVertexs[triangl[0]], zVertexs[triangl[1]], zVertexs[triangl[2]]});
                }

//                for (int[] triangl : triangles) {
//                    for (int vertexInPolygonInd = 1; vertexInPolygonInd < 3; vertexInPolygonInd++) {
//                        graphicsContext.strokeLine(
//                                resultPoints.get(triangl[vertexInPolygonInd - 1]).x,
//                                resultPoints.get(triangl[vertexInPolygonInd - 1]).y,
//                                resultPoints.get(triangl[vertexInPolygonInd]).x,
//                                resultPoints.get(triangl[vertexInPolygonInd]).y);
//                    }
//                    graphicsContext.strokeLine(
//                            resultPoints.get(triangl[2]).x,
//                            resultPoints.get(triangl[2]).y,
//                            resultPoints.get(triangl[0]).x,
//                            resultPoints.get(triangl[0]).y);
//                }
            } else {
                Vector3f[] normals = new Vector3f[3];
                normals[0] = (mesh.normals.get(0));
                normals[1] = (mesh.normals.get(1));
                normals[2] = (mesh.normals.get(2));
                Rasterization.fillTriangle(graphicsContext,
                        new int[]{(int) resultPoints.get(0).x, (int) resultPoints.get(1).x, (int) resultPoints.get(2).x},
                        new int[]{(int) resultPoints.get(0).y, (int) resultPoints.get(1).y, (int) resultPoints.get(2).y},
                        new Color[]{Color.BLUE, Color.BLUE, Color.BLUE},
                        normals,
                        new float[]{zVertexs[0], zVertexs[1], zVertexs[2]});
//                for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
//                    graphicsContext.strokeLine(
//                            resultPoints.get(vertexInPolygonInd - 1).x,
//                            resultPoints.get(vertexInPolygonInd - 1).y,
//                            resultPoints.get(vertexInPolygonInd).x,
//                            resultPoints.get(vertexInPolygonInd).y);
//                }
//
//                graphicsContext.strokeLine(
//                        resultPoints.get(nVerticesInPolygon - 1).x,
//                        resultPoints.get(nVerticesInPolygon - 1).y,
//                        resultPoints.get(0).x,
//                        resultPoints.get(0).y);
            }

        }
    }
}