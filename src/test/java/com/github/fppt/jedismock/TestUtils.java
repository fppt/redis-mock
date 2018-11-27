package com.github.fppt.jedismock;

import com.google.common.collect.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

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

    private void assertArgumentCheckThrowsException(BiConsumer<List<Slice>, Integer> check, List<Slice> args, int passValue, int failValue){
        check.accept(args, passValue);
        exception.expect(IllegalArgumentException.class);
        check.accept(args, failValue);
    }

    @Test
    public void testCheckArgumentsNumberEquals() {
        List<Slice> args = Lists.newArrayList(Slice.create(""), Slice.create(""));
        assertArgumentCheckThrowsException(Utils::checkArgumentsNumberEquals, args, 2, 1);
    }

    @Test
    public void testCheckArgumentsNumberGreater() {
        List<Slice> args = Lists.newArrayList(Slice.create(""), Slice.create(""));
        assertArgumentCheckThrowsException(Utils::checkArgumentsNumberGreater, args, 1, 2);
    }

    @Test
    public void testCheckArgumentsNumberFactor() {
        List<Slice> args = Lists.newArrayList(Slice.create(""), Slice.create(""), Slice.create(""));
        assertArgumentCheckThrowsException(Utils::checkArgumentsNumberFactor, args, 1, 2);
    }

    @Test
    public void testSerializeAndDeserialize() {
        Slice a = Slice.create("abcdef");
        Slice b = Utils.deserializeObject(Utils.serializeObject(a));
        assertArrayEquals(a.data(), b.data());
    }
}
