package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.RedisServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestZRemRangeByScore {

    private static final String ZSET_KEY = "myzset";

    private RedisServer server;
    private Jedis jedis;

    @BeforeEach
    public void start() throws IOException {
        server = RedisServer.newRedisServer();
        server.start();
        jedis = new Jedis(server.getHost(), server.getBindPort());
    }

    @AfterEach
    public void stop() {
        server.stop();
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItReturnsZeroForNonDefinedKey() {
        assertEquals(0, jedis.zremrangeByScore(ZSET_KEY, "-inf", "+inf"));
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItReturnsSetSizeWhenLowestAndHighestScoresSpecified() {
        // given
        jedis.zadd(ZSET_KEY, 1, "one");
        jedis.zadd(ZSET_KEY, 2, "two");
        jedis.zadd(ZSET_KEY, 3, "three");
        assertEquals(3, jedis.zrange(ZSET_KEY, 0, -1).size());

        // when
        final Long zremrangeByScoreResult = jedis.zremrangeByScore(ZSET_KEY, "-inf", "+inf");

        // then
        assertEquals(3, zremrangeByScoreResult);
        assertEquals(0, jedis.zrange(ZSET_KEY, 0, -1).size());
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItRemovesValueWhenIntervalSpecified() {
        // given
        jedis.zadd(ZSET_KEY, 1, "one");
        jedis.zadd(ZSET_KEY, 2, "two");
        jedis.zadd(ZSET_KEY, 3, "three");
        assertEquals(3, jedis.zrange(ZSET_KEY, 0, -1).size());

        // when
        final Long zremrangeByScoreResult = jedis.zremrangeByScore(ZSET_KEY, "-inf", "2");

        // then
        assertEquals(2, zremrangeByScoreResult);
        final Set<String> zrangeResult = jedis.zrange(ZSET_KEY, 0, -1);
        assertEquals(1, zrangeResult.size());
        assertArrayEquals(zrangeResult.toArray(), new String[]{"three"});
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItDoesNotRemoveValueWhenExclusiveIntervalSpecified() {
        // given
        jedis.zadd(ZSET_KEY, 1, "one");
        jedis.zadd(ZSET_KEY, 2, "two");
        jedis.zadd(ZSET_KEY, 3, "three");
        assertEquals(3, jedis.zrange(ZSET_KEY, 0, -1).size());

        // when
        final Long zremrangeByScoreResult = jedis.zremrangeByScore(ZSET_KEY, "-inf", "(2");

        // then
        assertEquals(1, zremrangeByScoreResult);
        final Set<String> zrangeResult = jedis.zrange(ZSET_KEY, 0, -1);
        assertEquals(2, zrangeResult.size());
        assertArrayEquals(zrangeResult.toArray(), new String[]{"three", "two"});
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItRemovesValuesAccordingToSpecifiedInterval() {
        // given
        jedis.zadd(ZSET_KEY, 1, "one");
        jedis.zadd(ZSET_KEY, 2, "two");
        jedis.zadd(ZSET_KEY, 3, "three");
        jedis.zadd(ZSET_KEY, 4, "four");
        jedis.zadd(ZSET_KEY, 5, "five");
        jedis.zadd(ZSET_KEY, 6, "six");
        jedis.zadd(ZSET_KEY, 7, "seven");
        jedis.zadd(ZSET_KEY, 8, "eight");
        jedis.zadd(ZSET_KEY, 9, "nine");
        jedis.zadd(ZSET_KEY, 10, "ten");
        assertEquals(10, jedis.zrange(ZSET_KEY, 0, -1).size());

        // when
        final Long zremrangeByScoreResult = jedis.zremrangeByScore(ZSET_KEY, 5, 8);

        // then
        assertEquals(4, zremrangeByScoreResult);
        final Set<String> zrangeResult = jedis.zrange(ZSET_KEY, 0, -1);
        assertEquals(6, zrangeResult.size());
        assertTrue(zrangeResult.containsAll(Arrays.asList("nine", "three", "one", "two", "four", "ten")));
    }

    @Test
    public void whenUsingZremrangeByScore_EnsureItThrowsExceptionsWhenStartAndEndHaveWrongFormat() {
        // given
        jedis.zadd(ZSET_KEY, 1, "one");
        jedis.zadd(ZSET_KEY, 2, "two");
        jedis.zadd(ZSET_KEY, 3, "three");

        // then
        assertThrows(JedisDataException.class,
                ()->jedis.zremrangeByScore(ZSET_KEY, "(dd", "(sd"));
        assertThrows(JedisDataException.class,
                ()->jedis.zremrangeByScore(ZSET_KEY, "1.e", "2.d"));
        assertThrows(RuntimeException.class,
                ()->jedis.zremrangeByScore(ZSET_KEY, "-INFINITY", "+INFINITY"));
    }
}
