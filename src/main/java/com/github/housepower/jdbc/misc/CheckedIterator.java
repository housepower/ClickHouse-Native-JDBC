package com.github.housepower.jdbc.misc;

/**
 * Copyright (C) 2018 SpectX
 * Created by Lauri Nõmme
 * 12.12.2018 16:11
 */
public interface CheckedIterator<T, E extends Throwable> {
    boolean hasNext() throws E;

    T next() throws E;
}
