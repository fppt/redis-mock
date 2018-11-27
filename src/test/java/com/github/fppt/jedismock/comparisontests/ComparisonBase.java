package com.github.fppt.jedismock.comparisontests;

import com.github.fppt.jedismock.RedisServer;
import com.github.fppt.jedismock.util.EmbeddedRedis;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.rules.ExpectedException;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class ComparisonBase {
    private static RedisServer fakeServer;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void startupRedisServers() throws IOException {
        //Start up the real redis server
        EmbeddedRedis.start();

        //Start up the fake redis server
        fakeServer = RedisServer.newRedisServer(EmbeddedRedis.PORT + 1);
        fakeServer.start();
    }

    @AfterClass
    public static void takeDownRedisServers(){
        //Kill the real redis server
        EmbeddedRedis.stop();

        //Kill the fake redis server
        fakeServer.stop();
    }

    @DataPoints
    public static Jedis[] jedis(){
        Jedis[] jedis = new Jedis[2];

        //Create real jedis connection
        jedis[0] = new Jedis("localhost", EmbeddedRedis.PORT);

        //Create mocked jedis connection
        jedis[1] = new Jedis(fakeServer.getHost(), fakeServer.getBindPort(), 1000000);

        return jedis;
    }
}
