package com.cgvsu.ation;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Matrix3f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindNormals {
    public static ArrayList<Vector3f> findNormals(Model m) {
        List<Polygon> polygons = m.polygons;
        List<Vector3f> vertices = m.vertices;

        ArrayList<Vector3f> temporaryNormals = new ArrayList<>();
        ArrayList<Vector3f> normals = new ArrayList<>();

        for (Polygon p : polygons) {
            temporaryNormals.add(FindNormals.findPolygonsNormals(vertices.get(p.getVertexIndices().get(0)),
                    vertices.get(p.getVertexIndices().get(1)), vertices.get(p.getVertexIndices().get(2))));
        }

        Map<Integer, Set<Vector3f>> vertexPolygonsMap = new HashMap<>();
        for (int j = 0; j < polygons.size(); j++) {
            List<Integer> vertexIndices = polygons.get(j).getVertexIndices();
            Vector3f vec = temporaryNormals.get(j);
            for (Integer index : vertexIndices) {
                vertexPolygonsMap.computeIfAbsent(index, k -> new HashSet<>()).add(vec);
            }
        }

        for (int i = 0; i < vertices.size(); i++) {
            normals.add(findVertexNormals(vertexPolygonsMap.get(i)));
        }

        return normals;
    }

    public static Vector3f findPolygonsNormals(Vector3f... vs) {
        Vector3f a = Vector3f.subtraction(vs[0], vs[1]);
        Vector3f b = Vector3f.subtraction(vs[0], vs[2]);

        Vector3f c = Vector3f.crossProduct(a, b);
        Matrix3f m = new Matrix3f(a, b, c);
        if (m.determinant() < 0) {
            c = Vector3f.crossProduct(b, a);
        }

        return c.normal();
    }

    public static Vector3f findVertexNormals(Set<Vector3f> vs) {
        float xs = 0, ys = 0, zs = 0;

        for (Vector3f v : vs) {
            xs += v.x;
            ys += v.y;
            zs += v.z;
        }

        xs /= vs.size();
        ys /= vs.size();
        zs /= vs.size();

        return new Vector3f(xs, ys, zs).normal();
    }
}
