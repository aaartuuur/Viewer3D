package com.cgvsu.model;

import com.cgvsu.exception.ArrayListException;
import com.cgvsu.math.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DelPolygons extends Exception {
    public static void delPolygons(ArrayList<Integer> polygonIndicesToDelete,
                                   ArrayList<Polygon> polygons,
                                   ArrayList<Vector3f> vertices) {
        checkListToDel(polygonIndicesToDelete, polygons);

        // Сортируем индексы полигонов для удаления в порядке убывания,
        // чтобы избежать смещения индексов при удалении
        Collections.sort(polygonIndicesToDelete, Collections.reverseOrder());

        // Удаляем полигоны по индексам
        for (int i : polygonIndicesToDelete) {
            polygons.remove(i);
        }

        // После удаления полигонов, обновляем модель
        // Удаляем вершины, которые больше не используются в полигонах
        updateModelAfterPolygonDeletion(polygons, vertices);
    }

    public static void checkListToDel(ArrayList<Integer> polygonIndicesToDelete, ArrayList<Polygon> polygons) {
        if (polygonIndicesToDelete.isEmpty()) {
            throw new ArrayListException("ArrayList is empty(");
        }
        for (int i : polygonIndicesToDelete) {
            if (i >= polygons.size() || i < 0) {
                throw new ArrayListException("Element of ArrayList is out of bounds");
            }
        }
    }

    private static void updateModelAfterPolygonDeletion(ArrayList<Polygon> polygons,
                                                        ArrayList<Vector3f> vertices) {
        // Определяем множество индексов вершин, которые все еще используются в полигонах
        Set<Integer> usedVertexIndices = new HashSet<>();
        for (Polygon polygon : polygons) {
            usedVertexIndices.addAll(polygon.getVertexIndices());
        }

        // Ищем вершины, которые больше не используются
        ArrayList<Integer> vertexIndicesToDelete = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (!usedVertexIndices.contains(i)) {
                vertexIndicesToDelete.add(i);
            }
        }

        // Удаляем неиспользуемые вершины и корректируем индексы в полигонах
        DelVertices.delVertices(vertexIndicesToDelete, vertices, polygons);
    }
}
