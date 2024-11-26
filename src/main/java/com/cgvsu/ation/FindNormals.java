package com.cgvsu.ation;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;

public class FindNormals {
    public static ArrayList<Vector3f> findNormals(List<Polygon> polygons, List<Vector3f> vertices) {
        ArrayList<Vector3f> temporaryNormals = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();

        for (Polygon p : polygons) {
            temporaryNormals.add(FindNormals.findPolygonsNormals(vertices.get(p.getVertexIndices().get(0)),
                    vertices.get(p.getVertexIndices().get(1)), vertices.get(p.getVertexIndices().get(2))));
        }

        for (int i = 0; i < vertices.size(); i++) {
            List<Vector3f> polygonNormalsList = new ArrayList<>();
            for (int j = 0; j < polygons.size(); j++) {
                if (polygons.get(j).getVertexIndices().contains(i)) {
                    polygonNormalsList.add(temporaryNormals.get(j));
                }
            }
            normals.add(FindNormals.findVertexNormals(polygonNormalsList));
        }

        return normals;
    }

    public static Vector3f findPolygonsNormals(Vector3f... vs) {
        Vector3f a = Vector3f.subtraction(vs[0], vs[1]);
        Vector3f b = Vector3f.subtraction(vs[0], vs[2]);

        Vector3f c = Vector3f.crossProduct(a, b);
        if (determinant(a, b, c) < 0) {
            c = Vector3f.crossProduct(b, a);
        }

        c.normalize();
        return c;
    }

    public static Vector3f findVertexNormals(List<Vector3f> vs) {
        float xs = 0, ys = 0, zs = 0;

        for (Vector3f v : vs) {
            xs += v.x;
            ys += v.y;
            zs += v.z;
        }

        xs /= vs.size();
        ys /= vs.size();
        zs /= vs.size();

        Vector3f v = new Vector3f(xs, ys, zs);
        v.normalize();
        return v;
    }

    public static double determinant(Vector3f a, Vector3f b, Vector3f c) {
        return a.x * (b.y * c.z) - a.y * (b.x * c.z - c.x * b.z) + a.z * (b.x * c.y - c.x * b.y);
    }
}
