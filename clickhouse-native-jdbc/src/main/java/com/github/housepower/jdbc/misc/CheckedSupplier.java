package com.github.housepower.jdbc.misc;

/**
 * Copyright (C) 2018 SpectX
 * Created by Lauri Nõmme
 * 12.12.2018 16:04
 */
public interface CheckedSupplier<R, E extends Throwable> {
    R get() throws E;
}
