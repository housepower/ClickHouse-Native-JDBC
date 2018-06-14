package com.github.housepower.jdbc.misc;

public class Container<T> {
    private final T left;
    private final T right;

    private boolean isLeft = true;

    public Container(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public void select(boolean isRight) {
        isLeft = (isLeft == isRight) ? !isRight : isLeft;
    }

    public T get() {
        return isLeft ? left : right;
    }
}
