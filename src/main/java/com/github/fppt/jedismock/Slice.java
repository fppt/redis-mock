package com.github.fppt.jedismock;

import com.google.auto.value.AutoValue;

import java.io.Serializable;

/**
 * Created by Xiaolu on 2015/4/23.
 */
@AutoValue
public abstract class Slice implements Comparable<Slice>, Serializable {
    public abstract byte[] data();

    public static Slice create(byte[] data){
        return new AutoValue_Slice(data);
    }

    public static Slice create(String data){
        return new AutoValue_Slice(data.getBytes().clone());
    }

    public int length(){
        return data().length;
    }

    @Override
    public String toString() {
        return new String(data());
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
