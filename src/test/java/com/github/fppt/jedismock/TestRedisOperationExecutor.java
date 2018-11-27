package com.github.fppt.jedismock;

import com.github.fppt.jedismock.commands.RedisOperationExecutor;
import com.github.fppt.jedismock.exception.EOFException;
import com.github.fppt.jedismock.exception.ParseErrorException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class TestRedisOperationExecutor {

    private static final String CRLF = "\r\n";
    private static RedisOperationExecutor executor;

    private static String bulkString(CharSequence param) {
        return "$" + param.length() + CRLF + param.toString() + CRLF;
    }

    private static String array(CharSequence ...params) {
        StringBuilder builder = new StringBuilder();
        builder.append('*').append(params.length).append(CRLF);
        for (CharSequence param : params) {
            if (param == null) {
                builder.append("$-1").append(CRLF);
            } else {
                builder.append(bulkString(param));
            }
        }
        return builder.toString();
    }

    private void assertCommandEquals(String expect, String command) throws ParseErrorException, EOFException {
        assertEquals(bulkString(expect), executor.execCommand(RedisCommandParser.parse(command)).toString());
    }

    private void assertCommandEquals(long expect, String command) throws ParseErrorException, EOFException {
        assertEquals(Response.integer(expect), executor.execCommand(RedisCommandParser.parse(command)));
    }

    private void assertCommandNull(String command) throws ParseErrorException, EOFException {
        assertEquals(Response.NULL, executor.execCommand(RedisCommandParser.parse(command)));
    }

    private void assertCommandOK(String command) throws ParseErrorException, EOFException {
        assertEquals(Response.OK, executor.execCommand(RedisCommandParser.parse(command)));
    }

    private void assertCommandError(String command) throws ParseErrorException, EOFException {
        assertEquals('-', executor.execCommand(RedisCommandParser.parse(command)).data()[0]);
    }

    @Before
    public void initCommandExecutor() {
        //TODO: Mock out the client here
        executor = new RedisOperationExecutor(new RedisBase(), null);
    }

    @Test
    public void testSetAndGet() throws ParseErrorException, EOFException {
        assertCommandNull(array("GET", "ab"));
        assertCommandOK(array("SET", "ab", "abc"));
        assertCommandEquals("abc", array("GET", "ab"));
        assertCommandOK(array("SET", "ab", "abd"));
        assertCommandEquals("abd", array("GET", "ab"));
        assertCommandNull(array("GET", "ac"));
    }

    @Test
    public void testWrongNumberOfArguments() throws ParseErrorException, EOFException {
        assertCommandError(array("SET", "ab"));
        assertCommandError(array("pfcount"));
    }

    @Test
    public void testUnknownCommand() throws ParseErrorException, EOFException {
        assertCommandError(array("unknown"));
    }

    @Test
    public void testExpire() throws ParseErrorException, InterruptedException, EOFException {
        assertCommandEquals(0, array("expire", "ab", "1"));
        assertCommandOK(array("SET", "ab", "abd"));
        assertCommandEquals(1, array("expire", "ab", "1"));
        assertCommandEquals("abd", array("GET", "ab"));
        assertCommandError(array("expire", "ab", "a"));
        Thread.sleep(1000);
        assertCommandNull(array("GET", "ab"));
    }

    @Test
    public void testTTL() throws ParseErrorException, InterruptedException, EOFException {
        assertCommandEquals(-2, array("ttl", "ab"));
        assertCommandOK(array("SET", "ab", "abd"));
        assertCommandEquals(-1, array("ttl", "ab"));
        assertCommandEquals(1, array("expire", "ab", "2"));
        assertCommandEquals(2, array("ttl", "ab"));
        Thread.sleep(1000);
        assertCommandEquals(1, array("ttl", "ab"));
        Thread.sleep(1000);
        assertCommandEquals(-2, array("ttl", "ab"));
    }

    @Test
    public void testPTTL() throws ParseErrorException, InterruptedException, EOFException {
        assertCommandEquals(-2, array("pttl", "ab"));
        assertCommandOK(array("SET", "ab", "abd"));
        assertCommandEquals(-1, array("pttl", "ab"));
        assertCommandEquals(1, array("expire", "ab", "2"));
        assertTrue(executor.execCommand(RedisCommandParser.parse(array("pttl", "ab"))).compareTo(Response.integer(1900L)) > 0);
        Thread.sleep(1100);
        assertTrue(executor.execCommand(RedisCommandParser.parse(array("pttl", "ab"))).compareTo(Response.integer(999L)) < 0);
        Thread.sleep(1000);
        assertCommandEquals(-2, array("pttl", "ab"));
    }

    @Test
    public void testIncr() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("incr", "a"));
        assertCommandEquals(2, array("incr", "a"));
        assertCommandOK(array("set", "a", "b"));
        assertCommandError(array("incr", "a"));
    }

    @Test
    public void testIncrBy() throws ParseErrorException, EOFException {
        assertCommandEquals(5, array("incrby", "a", "5"));
        assertCommandEquals(11, array("incrby", "a", "6"));
        assertCommandOK(array("set", "a", "b"));
        assertCommandError(array("incrby", "a", "1"));
    }

    @Test
    public void testDecr() throws ParseErrorException, EOFException {
        assertCommandEquals(-1, array("decr", "a"));
        assertCommandEquals(-2, array("decr", "a"));
        assertCommandOK(array("set", "a", "b"));
        assertCommandError(array("decr", "a"));
    }

    @Test
    public void testDecrBy() throws ParseErrorException, EOFException {
        assertCommandEquals(-5, array("decrby", "a", "5"));
        assertCommandEquals(-11, array("decrby", "a", "6"));
        assertCommandOK(array("set", "a", "b"));
        assertCommandError(array("decrby", "a", "1"));
    }

    @Test
    public void testHll() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("pfadd", "a", "b", "c"));
        assertCommandEquals(0, array("pfadd", "a", "b", "c"));
        assertCommandEquals(2, array("pfcount", "a"));
        assertCommandEquals(0, array("pfcount", "b"));
        assertCommandEquals(1, array("pfadd", "b", "c", "d"));
        assertCommandEquals(3, array("pfcount", "a", "b"));
        assertCommandOK(array("pfmerge", "c"));
        assertCommandEquals(0, array("pfcount", "c"));
        assertCommandOK(array("pfmerge", "a", "b"));
        assertCommandEquals(3, array("pfcount", "a"));
        assertCommandOK(array("set", "a", "b"));
        assertCommandError(array("pfcount", "a"));
        assertCommandError(array("pfmerge", "a"));
        assertCommandError(array("pfmerge", "b", "a"));
        assertCommandError(array("pfadd", "a", "b"));
    }

    @Test
    public void testAppend() throws ParseErrorException, EOFException {
        assertCommandEquals(3, array("append", "ab", "abc"));
        assertCommandEquals(6, array("append", "ab", "abc"));
        assertCommandEquals("abcabc", array("GET", "ab"));
    }

    @Test
    public void testSetAndGetBit() throws ParseErrorException, EOFException {
        assertCommandEquals(0, array("getbit", "mykey", "7"));
        assertCommandEquals(0, array("setbit", "mykey", "7", "1"));
        assertCommandEquals(1, array("getbit", "mykey", "7"));
        assertCommandEquals(0, array("getbit", "mykey", "6"));
        assertCommandEquals(1, array("setbit", "mykey", "7", "0"));
        assertEquals(Response.bulkString(Slice.create(new byte[]{0})),
                executor.execCommand(RedisCommandParser.parse(array("get", "mykey"))));
        assertCommandEquals(0, array("setbit", "mykey", "33", "1"));
        assertEquals(Response.bulkString(Slice.create(new byte[]{0, 0, 0, 0, 2})),
                executor.execCommand(RedisCommandParser.parse(array("get", "mykey"))));
        assertCommandEquals(0, array("setbit", "mykey", "22", "1"));
        assertCommandEquals(0, array("getbit", "mykey", "117"));
        assertCommandError(array("getbit", "mykey", "a"));
        assertCommandError(array("getbit", "mykey"));
        assertCommandError(array("setbit", "mykey", "a", "1"));
        assertCommandError(array("setbit", "mykey", "1"));
        assertCommandError(array("setbit", "mykey", "1", "a"));
        assertCommandError(array("setbit", "mykey", "1", "2"));
    }

    @Test
    public void testSetex() throws ParseErrorException, EOFException {
        assertCommandOK(array("SETex", "ab", "100", "k"));
        assertCommandEquals(100, array("ttl", "ab"));
        assertCommandError(array("SETex", "ab", "10a", "k"));
    }

    @Test
    public void testPsetex() throws ParseErrorException, EOFException {
        assertCommandOK(array("pSETex", "ab", "99", "k"));
        assertTrue(executor.execCommand(RedisCommandParser.parse(array("pttl", "ab"))).compareTo(Response.integer(90)) > 0);
        assertTrue(executor.execCommand(RedisCommandParser.parse(array("pttl", "ab"))).compareTo(Response.integer(99)) <= 0);
        assertCommandError(array("pSETex", "ab", "10a", "k"));
    }

    @Test
    public void testSetnx() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("setnx", "k", "vvv"));
        assertCommandEquals("vvv", array("get", "k"));
        assertCommandEquals(0, array("setnx", "k", "ggg"));
        assertCommandEquals("vvv", array("get", "k"));
    }

    @Test
    public void testMset() throws ParseErrorException, EOFException {
        assertCommandOK(array("mset", "k1", "a", "k2", "b"));
        assertCommandEquals("a", array("GET", "k1"));
        assertCommandEquals("b", array("GET", "k2"));
        assertCommandError(array("mset", "k1", "a", "k2"));
    }

    @Test
    public void testMget() throws ParseErrorException, EOFException {
        assertCommandOK(array("SET", "a", "abc"));
        assertCommandOK(array("SET", "b", "abd"));

        assertEquals(array("abc", "abd", null),
                executor.execCommand(RedisCommandParser.parse(array("mget", "a", "b", "c"))).toString());
    }

    @Test
    public void testGetset() throws ParseErrorException, EOFException {
        assertCommandNull(array("getSET", "a", "abc"));
        assertCommandEquals("abc", array("getSET", "a", "abd"));
    }

    @Test
    public void testStrlen() throws ParseErrorException, EOFException {
        assertCommandEquals(0, array("strlen", "a"));
        assertCommandOK(array("SET", "a", "abd"));
        assertCommandEquals(3, array("strlen", "a"));
    }

    @Test
    public void testDel() throws ParseErrorException, EOFException {
        assertCommandOK(array("set", "a", "v"));
        assertCommandOK(array("set", "b", "v"));
        assertCommandEquals(2, array("del", "a", "b", "c"));
        assertCommandNull(array("get", "a"));
        assertCommandNull(array("get", "b"));
    }

    @Test
    public void testExists() throws ParseErrorException, EOFException {
        assertCommandOK(array("set", "a", "v"));
        assertCommandEquals(1, array("exists", "a"));
        assertCommandEquals(0, array("exists", "b"));
    }

    @Test
    public void testExpireAt() throws ParseErrorException, EOFException {
        assertCommandOK(array("set", "a", "v"));
        assertCommandEquals(1, array("expireat", "a", "1293840000"));
        assertCommandEquals(0, array("exists", "a"));
        assertCommandOK(array("set", "a", "v"));
        long now = System.currentTimeMillis() / 1000 + 5;
        assertCommandEquals(1, array("expireat", "a", String.valueOf(now)));
        assertCommandEquals(5, array("ttl", "a"));
        assertCommandError(array("expireat", "a", "a"));
    }

    @Test
    public void testPexpireAt() throws ParseErrorException, EOFException {
        assertCommandOK(array("set", "a", "v"));
        assertCommandEquals(1, array("pexpireat", "a", "1293840000000"));
        assertCommandEquals(0, array("exists", "a"));
        assertCommandEquals(0, array("pexpireat", "a", "1293840000000"));
        assertCommandOK(array("set", "a", "v"));
        long now = System.currentTimeMillis() + 5000;
        assertCommandEquals(1, array("pexpireat", "a", String.valueOf(now)));
        assertCommandEquals(5, array("ttl", "a"));
        assertCommandError(array("pexpireat", "a", "a"));
    }

    @Test
    public void testPexpire() throws ParseErrorException, EOFException {
        assertCommandOK(array("set", "a", "v"));
        assertCommandEquals(1, array("pexpire", "a", "1500000"));
        assertCommandEquals(1500, array("ttl", "a"));
    }

    @Test
    public void testLpush() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("lpush", "mylist", "!"));
        assertCommandEquals(3, array("lpush", "mylist", "world", "hello"));
        assertEquals(array("hello", "world", "!"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "0", "-1"))).toString());
        assertCommandOK(array("set", "a", "v"));
        assertCommandError(array("lpush", "a", "1"));
    }

    @Test
    public void testLrange() throws ParseErrorException, EOFException {
        assertEquals(array(),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "0", "-1"))).toString());
        assertCommandEquals(3, array("lpush", "mylist", "1", "2", "3"));
        assertEquals(array("3", "2", "1"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "0", "-1"))).toString());
        assertEquals(array("3", "2", "1"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "-10", "10"))).toString());
        assertEquals(array("2"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "1", "-2"))).toString());
        assertEquals(array(),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "10", "-10"))).toString());
        assertCommandError(array("lrange", "mylist", "a", "-1"));
        assertCommandOK(array("set", "a", "v"));
        assertCommandError(array("lrange", "a", "0", "-1"));
    }

    @Test
    public void testLlen() throws ParseErrorException, EOFException {
        assertCommandEquals(0, array("llen", "a"));
        assertCommandEquals(3, array("lpush", "mylist", "3", "2", "1"));
        assertCommandEquals(3, array("llen", "mylist"));
        assertCommandOK(array("set", "a", "v"));
        assertCommandError(array("llen", "a"));
    }

    @Test
    public void testLpushx() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("lpush", "a", "1"));
        assertCommandEquals(2, array("lpushx", "a", "2"));
        assertEquals(array("2", "1"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "a", "0", "-1"))).toString());
        assertCommandEquals(0, array("lpushx", "b", "1"));
        assertCommandOK(array("set", "a", "v"));
        assertCommandError(array("lpushx", "a", "1"));
    }

    @Test
    public void testLpop() throws ParseErrorException, EOFException {
        assertCommandEquals(2, array("lpush", "list", "2", "1"));
        assertCommandEquals("1", array("lpop", "list"));
        assertCommandEquals("2", array("lpop", "list"));
        assertCommandNull(array("lpop", "list"));
        assertCommandNull(array("lpop", "notexist"));
        assertCommandOK(array("set", "key", "value"));
        assertCommandError(array("lpop", "key"));
    }

    @Test
    public void testLindex() throws ParseErrorException, EOFException {
        assertCommandEquals(2, array("lpush", "list", "1", "2"));
        assertCommandEquals("2", array("lindex", "list", "0"));
        assertCommandEquals("1", array("lindex", "list", "-1"));
        assertCommandNull(array("lindex", "list", "2"));
        assertCommandNull(array("lindex", "list", "-3"));
        assertCommandError(array("lindex", "list", "a"));
        assertCommandNull(array("lindex", "notexist", "0"));
        assertCommandOK(array("set", "key", "value"));
        assertCommandError(array("lindex", "key", "1"));
    }

    @Test
    public void testRpush() throws ParseErrorException, EOFException {
        assertCommandEquals(1, array("rpush", "mylist", "!"));
        assertCommandEquals(3, array("rpush", "mylist", "world", "hello"));
        assertEquals(array("!", "world", "hello"),
                executor.execCommand(RedisCommandParser.parse(array("lrange", "mylist", "0", "-1"))).toString());
        assertCommandOK(array("set", "a", "v"));
        assertCommandError(array("rpush", "a", "1"));
    }
}
