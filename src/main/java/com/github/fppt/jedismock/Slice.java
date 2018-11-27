package com.github.fppt.jedismock;

import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Xiaolu on 2015/4/23.
 */
@AutoValue
public abstract class Slice implements Comparable<Slice>, Serializable {
    private static final String RESERVED_SLICE_NAME = "Reserved String In Jedis Mock";
    private static final byte[] RESERVED_SLICE_BYTES = RESERVED_SLICE_NAME.getBytes();
    private static Slice RESERVED_SLICE = null;

    private static final long serialVersionUID = 247772234876073528L;
    public abstract byte[] data();

    public static Slice create(byte[] data){
        if(Arrays.equals(data, RESERVED_SLICE_BYTES)){
           throw new RuntimeException("Cannot create key/value in mock due to [" + RESERVED_SLICE_NAME + "] being reserved");
        }
        return new AutoValue_Slice(data);
    }

    public static Slice create(String data){
        return create(data.getBytes().clone());
    }

    public static synchronized Slice reserved(){
        if (RESERVED_SLICE == null){
            RESERVED_SLICE = new AutoValue_Slice(RESERVED_SLICE_BYTES);
        }
        return RESERVED_SLICE;
    }

    public int length(){
        return data().length;
    }

    @Override
    public String toString() {
        return new String(data());
    }

    @Override
    public boolean equals(Object b) {
        return b instanceof Slice && Arrays.equals(data(), ((Slice) b).data());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data());
    }

    public int compareTo(Slice b) {
        int len1 = data().length;
        int len2 = b.data().length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            byte b1 = data()[k];
            byte b2 = b.data()[k];
            if (b1 != b2) {
                return b1 - b2;
            }
            k++;
        }
        return len1 - len2;
    }
}
