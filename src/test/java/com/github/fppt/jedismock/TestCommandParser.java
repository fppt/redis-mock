package com.github.fppt.jedismock;

import com.github.fppt.jedismock.exception.EOFException;
import com.github.fppt.jedismock.exception.ParseErrorException;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class TestCommandParser {

    @Test
    public void testConsumeCharacter() throws ParseErrorException, EOFException {
        InputStream stream = new ByteArrayInputStream("a".getBytes());
        assertEquals(SliceParser.consumeByte(stream), 'a');
    }

    @Test
    public void testExpectCharacter() throws ParseErrorException, EOFException {
        InputStream stream = new ByteArrayInputStream("a".getBytes());
        SliceParser.expectByte(stream, (byte) 'a');
    }

    @Test
    public void testConsumeLong() throws ParseErrorException {
        InputStream stream = new ByteArrayInputStream("12345678901234\r".getBytes());
        assertEquals(SliceParser.consumeLong(stream), 12345678901234L);
    }

    @Test
    public void testConsumeString() throws ParseErrorException {
        InputStream stream = new ByteArrayInputStream("abcd".getBytes());
        assertEquals(SliceParser.consumeSlice(stream, 4).toString(), "abcd");
    }

    @Test
    public void testConsumeCount1() throws ParseErrorException, EOFException {
        InputStream stream = new ByteArrayInputStream("*12\r\n".getBytes());
        assertEquals(SliceParser.consumeCount(stream), 12L);
    }

    @Test
    public void testConsumeCount2() throws EOFException {
        InputStream stream = new ByteArrayInputStream("*2\r".getBytes());
        try {
            SliceParser.consumeCount(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }


    @Test
    public void testConsumeParameter() throws ParseErrorException {
        InputStream stream = new ByteArrayInputStream("$5\r\nabcde\r\n".getBytes());
        assertEquals(SliceParser.consumeParameter(stream).toString(), "abcde");
    }

    @Test
    public void testParse() throws ParseErrorException, EOFException {
        RedisCommand cmd = RedisCommandParser.parse("*3\r\n$0\r\n\r\n$4\r\nabcd\r\n$2\r\nef\r\n");
        assertEquals(cmd.parameters().get(0).toString(), "");
        assertEquals(cmd.parameters().get(1).toString(), "abcd");
        assertEquals(cmd.parameters().get(2).toString(), "ef");
    }

    @Test
    public void testConsumeCharacterError() throws ParseErrorException {
        InputStream stream = new ByteArrayInputStream("".getBytes());
        try {
            SliceParser.consumeByte(stream);
            assertTrue(false);
        } catch (EOFException e) {
            // OK
        }
    }

    @Test
    public void testExpectCharacterError1() throws EOFException {
        InputStream stream = new ByteArrayInputStream("a".getBytes());
        try {
            SliceParser.expectByte(stream, (byte) 'b');
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testExpectCharacterError2() throws ParseErrorException {
        InputStream in = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };
        try {
            SliceParser.expectByte(in, (byte) 'b');
            assertTrue(false);
        } catch (EOFException e) {
            // OK
        }
    }

    @Test
    public void testConsumeLongError1() {
        InputStream stream = new ByteArrayInputStream("\r".getBytes());
        try {
            SliceParser.consumeLong(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeLongError2() {
        InputStream stream = new ByteArrayInputStream("100a".getBytes());
        try {
            SliceParser.consumeLong(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeLongError3() {
        InputStream stream = new ByteArrayInputStream("".getBytes());
        try {
            SliceParser.consumeLong(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeStringError() {
        InputStream stream = new ByteArrayInputStream("abc".getBytes());
        try {
            SliceParser.consumeSlice(stream, 4);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeCountError1() throws EOFException {
        InputStream stream = new ByteArrayInputStream("$12\r\n".getBytes());
        try {
            SliceParser.consumeCount(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeCountError2() throws EOFException {
        InputStream stream = new ByteArrayInputStream("*12\ra".getBytes());
        try {
            SliceParser.consumeCount(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeParameterError1() {
        InputStream stream = new ByteArrayInputStream("$4\r\nabcde\r\n".getBytes());
        try {
            SliceParser.consumeParameter(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeParameterError2() {
        InputStream stream = new ByteArrayInputStream("$4\r\nabc\r\n".getBytes());
        try {
            SliceParser.consumeParameter(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeParameterError3() {
        InputStream stream = new ByteArrayInputStream("$4\r\nabc".getBytes());
        try {
            SliceParser.consumeParameter(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testConsumeParameterError4() {
        InputStream stream = new ByteArrayInputStream("$4\r".getBytes());
        try {
            SliceParser.consumeParameter(stream);
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }

    @Test
    public void testParseError() throws ParseErrorException, EOFException {
        try {
            RedisCommandParser.parse("*0\r\n");
            assertTrue(false);
        } catch (ParseErrorException e) {
            // OK
        }
    }
}
