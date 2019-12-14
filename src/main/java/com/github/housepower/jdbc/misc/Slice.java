package com.github.housepower.jdbc.misc;

import java.util.Arrays;

/**
 * Slice Object just like slice of Go
 */
public class Slice {
    private Object []array;

    private int capacity;
    private int offset;
    private int pos;

    public Slice(int capacity) {
        this.offset = 0;
        this.pos = 0;

        this.array = new Object[capacity];
        this.capacity = capacity;
    }

    public Slice(Object []array) {
        this.offset = 0;
        this.pos = array.length;

        this.array = array;
        this.capacity = array.length;
    }

    public Slice(Slice slice, int offset, int pos) {
        this.array = slice.array;

        this.capacity = slice.capacity;
        this.offset = offset;
        this.pos = pos;
    }


    public int size() {
        return pos - offset;
    }

    public Slice sub(int offsetAdd, int posAdd) {
        return new Slice(this, offset + offsetAdd, offset + posAdd);
    }

    public Object get(int index) {
        return array[index + offset];
    }

    public void add(Object object) {
        if (pos >= capacity) {
            grow(capacity);
        }
        array[pos] = object;
        pos ++;
    }

    public void set(int index, Object object) {
        array[offset + index] = object;
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = array.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        array = Arrays.copyOf(array, newCapacity);
        capacity = newCapacity;
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
               Integer.MAX_VALUE :
               MAX_ARRAY_SIZE;
    }
}
