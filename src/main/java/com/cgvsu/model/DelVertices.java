package com.cgvsu.model;

import com.cgvsu.exception.ArrayListException;
import com.cgvsu.math.Vector3f;

import java.util.ArrayList;

public class DelVertices extends Exception{
    public static void delVertices(ArrayList<Integer> vertexIndicesToDelete, ArrayList<Vector3f> vertices, ArrayList<Polygon> polygons){
        checkListToDel(vertexIndicesToDelete, vertices);
        for (int i : vertexIndicesToDelete) {//идем по листу с индексами вершин которые надо удалить
            vertices.remove(i);

            for (int j = polygons.size() - 1; j >= 0; j--) {
                Polygon polygon = polygons.get(j);
                ArrayList<Integer> ind = polygon.getVertexIndices();
                if (ind.contains(i)) {// если лист вершин полигона содержит вершину, которая удаляется, то и удаляется полигон
                    polygons.remove(j);
                } else {
                    for (int m = 0; m < ind.size(); m++) { // сдвигаем индексы вершин после удаления
                        int currInd = ind.get(m);
                        if (currInd > i) {
                            ind.set(m, currInd - 1);
                        }
                    }
                }
            }
        }
    }

    public static void checkListToDel(ArrayList<Integer> vertexIndicesToDelete, ArrayList<Vector3f> vertices){
        if (vertexIndicesToDelete.isEmpty()){
            throw new ArrayListException("ArrayList is empty(");
        }
        for (int i : vertexIndicesToDelete){
            if (i > vertices.size() && i < 0){
                throw new ArrayListException("Element of ArrayList > size of it");
            }
        }
    }
}
