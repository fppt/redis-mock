package com.github.fppt.jedismock;

import com.github.fppt.jedismock.server.Slice;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class TestUtils {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testCloseQuietly() {
        Utils.closeQuietly(null);
        Utils.closeQuietly(new InputStream() {
            @Override
            public int read() {
                return 0;
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
    }


    @Test
    public void testSerializeAndDeserialize() {
        Slice a = Slice.create("abcdef");
        Slice b = Utils.deserializeObject(Utils.serializeObject(a));
        assertArrayEquals(a.data(), b.data());
    }
}
