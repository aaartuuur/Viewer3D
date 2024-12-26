package com.cgvsu.affine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.render_engine.GraphicConveyor;

public class TranslationModel {
    public static void move(Matrix4f transposeMatrix, Model model) {
        for (Vector3f vertex : model.vertices) {
            Vector3f newVertex = GraphicConveyor.multiplyMatrix4ByVector3(transposeMatrix, vertex);
            vertex.x = newVertex.x;
            vertex.y = newVertex.y;
            vertex.z = newVertex.z;
        }

    }
}