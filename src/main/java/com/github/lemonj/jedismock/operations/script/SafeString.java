package com.github.lemonj.jedismock.operations.script;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * create by lmj for string
 **/
public class SafeString implements Comparable<SafeString>, Serializable {

    private static final long serialVersionUID = -8770835877491298225L;

    private static final char[] CHARS = "0123456789abcdef".toCharArray();

    private transient ByteBuffer buffer;

    public SafeString(byte[] bytes) {
        this.buffer = ByteBuffer.wrap(requireNonNull(bytes));
    }

    public SafeString(ByteBuffer buffer) {
        this.buffer = requireNonNull(buffer);
    }

    public byte[] getBytes() {
        ByteBuffer copy = buffer.duplicate();
        byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return bytes;
    }

    public ByteBuffer getBuffer() {
        return buffer.duplicate();
    }

    public int length() {
        return buffer.remaining();
    }

    @Override
    public int hashCode() {
        return Objects.hash(buffer);
    }

    @Override
    public int compareTo(SafeString other) {
        return compare(this.getBytes(), other.getBytes());
    }

    @Override
    public String toString() {
        return UTF_8.decode(buffer.duplicate()).toString();
    }

    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = getBytes();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            sb.append(CHARS[v >>> 4]).append(CHARS[v & 0x0F]);
        }
        return sb.toString();
    }

    public static SafeString safeString(String str) {
        return new SafeString(UTF_8.encode(requireNonNull(str)));
    }

    public static SafeString fromHexString(String string) {
        byte[] array = new byte[string.length() / 2];
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) Integer.parseInt(string.substring((i * 2), (i * 2) + 2), 16);
        }
        return new SafeString(array);
    }

    public static List<SafeString> safeAsList(String... strs) {
        return Stream.of(requireNonNull(strs)).map(SafeString::safeString).collect(toList());
    }

    public static SafeString append(SafeString stringA, SafeString stringB) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(requireNonNull(stringA).length() + requireNonNull(stringB).length());
        byteBuffer.put(stringA.getBytes());
        byteBuffer.put(stringB.getBytes());
        byteBuffer.rewind();
        return new SafeString(byteBuffer);
    }

    private int compare(byte[] left, byte[] right) {
        for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        byte[] bytes = getBytes();
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private void readObject(ObjectInputStream input) throws IOException {
        int length = input.readInt();
        byte[] bytes = new byte[length];
        input.read(bytes);
        this.buffer = ByteBuffer.wrap(bytes);
    }
}
