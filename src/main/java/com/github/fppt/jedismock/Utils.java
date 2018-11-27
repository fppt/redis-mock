package com.github.fppt.jedismock;

import com.github.fppt.jedismock.exception.WrongValueTypeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by Xiaolu on 2015/4/21.
 */
public class Utils {

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkArgumentsNumberEquals(List<Slice> args, int expect) {
        if (args.size() != expect) {
            throw new IllegalArgumentException("The number of arguments [" + args.size() + "] does not match the expected value [" + expect + "]");
        }
    }

    public static void checkArgumentsNumberGreater(List<Slice> args, int expect) {
        if (args.size() <= expect) {
            throw new IllegalArgumentException("The number of arguments [" + args.size() + "] is less than the expected value [" + expect + "]");
        }
    }

    public static void checkArgumentsNumberFactor(List<Slice> args, int factor) {
        if (args.size() % factor != 0) {
            throw new IllegalArgumentException("The number of arguments [" + args.size() + "] is not a factor of the expected value [" + factor + "]");
        }
    }

    public static Slice serializeObject(Object o){
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(o);
            Slice encode = Slice.create(bo.toByteArray());
            oo.close();
            bo.close();
            return encode;
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> T deserializeObject(Slice data){
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(data.data());
            ObjectInputStream oi = new ObjectInputStream(bi);
            T ret = (T) oi.readObject();
            oi.close();
            bi.close();
            return ret;
        } catch (IOException | ClassNotFoundException e){
            throw new WrongValueTypeException("WRONGTYPE Key is not a valid HyperLogLog string value.");
        }
    }

    public static long convertToLong(String value){
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new WrongValueTypeException("ERR value is not an integer or out of range");
        }
    }

    public static byte convertToByte(String value){
        try {
            byte bit = Byte.parseByte(value);
            if (bit != 0 && bit != 1) {
                throw new NumberFormatException();
            }
            return bit;
        } catch (NumberFormatException e) {
            throw new WrongValueTypeException("ERR bit is not an integer or out of range");
        }
    }

    public static int convertToNonNegativeInteger(String value){
        try {
            int pos = Integer.parseInt(value);
            if(pos < 0) throw new NumberFormatException("Int less than 0");
            return pos;
        } catch (NumberFormatException e) {
            throw new WrongValueTypeException("ERR bit offset is not an integer or out of range");
        }
    }

    public static int convertToInteger(String value){
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new WrongValueTypeException("ERR bit offset is not an integer or out of range");
        }
    }
}
