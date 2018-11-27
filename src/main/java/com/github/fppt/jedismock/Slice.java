package com.github.fppt.jedismock;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Xiaolu on 2015/4/23.
 */
public class Slice implements Comparable<Slice>, Serializable {
    private static final long serialVersionUID = 247772234876073528L;

    private final byte[] data;
    //wat

    public Slice(byte[] data) {
        this.data = data.clone();
    }

    public Slice(String data) {
        this.data = data.getBytes().clone();
    }

    public byte[] data() {
        return this.data.clone();
    }

    public int length() {
        return this.data.length;
    }

    @Override
    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object b) {
        return b instanceof Slice && Arrays.equals(data, ((Slice) b).data());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public int compareTo(Slice b) {
        int len1 = data.length;
        int len2 = b.data.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            byte b1 = data[k];
            byte b2 = b.data[k];
            if (b1 != b2) {
                return b1 - b2;
            }
            k++;
        }
        return len1 - len2;
    }
}
