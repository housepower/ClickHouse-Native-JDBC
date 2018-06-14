package com.github.housepower.jdbc.tool;

import java.io.IOException;

import com.github.housepower.jdbc.buffer.BuffedReader;

public class FragmentBuffedReader implements BuffedReader {

    private int fragmentPos;

    private int bytesPosition;

    private final byte[][] fragments;

    public FragmentBuffedReader(byte[]... fragments) {
        this.fragments = fragments;
    }

    @Override
    public int readBinary() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readBinary(byte[] bytes) throws IOException {

        for (int i = 0; i < bytes.length; ) {
            if (bytesPosition == fragments[fragmentPos].length) {
                fragmentPos++;
                bytesPosition = 0;
            }


            byte[] fragment = fragments[fragmentPos];

            int pending = bytes.length - i;
            int fillLength = Math.min(pending, fragment.length - bytesPosition);

            if (fillLength > 0) {
                System.arraycopy(fragment, bytesPosition, bytes, i, fillLength);

                i += fillLength;
                bytesPosition += fillLength;
            }
        }
        return bytes.length;
    }
}
