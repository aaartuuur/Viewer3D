package com.cgvsu.affine;

public interface DataList<T> {
    void add(AffineTransform at);
    void remove(int index);
    void remove(AffineTransform at);
    void set(int index, AffineTransform at);
    T get(int index);
}
