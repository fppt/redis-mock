package com.github.fppt.jedismock;

import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.net.BindException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Xiaolu on 2015/4/18.
 */
public class TestRedisServer {

    @Test
    public void testBindPort() throws IOException {
        RedisServer server = RedisServer.newRedisServer(8080);
        server.start();
        assertEquals(server.getBindPort(), 8080);
        server.stop();
    }

    @Test
    public void testBindRandomPort() throws IOException {
        RedisServer server = RedisServer.newRedisServer();
        server.start();
        server.stop();
    }

    @Test
    public void testBindErrorPort() throws IOException {
        RedisServer server = RedisServer.newRedisServer(100000);
        try {
            server.start();
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testBindUsedPort() throws IOException {
        RedisServer server1 = RedisServer.newRedisServer();
        server1.start();
        RedisServer server2 = RedisServer.newRedisServer(server1.getBindPort());
        try {
            server2.start();
            assertTrue(false);
        } catch (BindException e) {
            // OK
        }
    }

    @Test
    public void testSlave() throws IOException {
        RedisServer master = RedisServer.newRedisServer();
        RedisServer slave = RedisServer.newRedisServer();
        master.setSlave(slave);
        master.start();
        slave.start();
        Jedis jedis1 = new Jedis(master.getHost(), master.getBindPort());
        Jedis jedis2 = new Jedis(slave.getHost(), slave.getBindPort());
        assertEquals(jedis1.set("ab", "cd"), "OK");
        assertEquals(jedis2.get("ab"), "cd");
        jedis1.disconnect();
        jedis2.disconnect();
        master.stop();
        slave.stop();
    }

    @Test
    public void testDelInMasterSlave() throws IOException {
        RedisServer master = RedisServer.newRedisServer();
        RedisServer slave = RedisServer.newRedisServer();
        master.setSlave(slave);
        master.start();
        slave.start();
        Jedis jedis1 = new Jedis(master.getHost(), master.getBindPort());
        Jedis jedis2 = new Jedis(slave.getHost(), slave.getBindPort());
        assertEquals(jedis1.set("a", "b"), "OK");
        assertEquals(jedis1.del("a"), (Long) 1L);
        assertEquals(jedis2.exists("a"), false);
        jedis1.disconnect();
        jedis2.disconnect();
        master.stop();
        slave.stop();
    }

    @Test
    public void testExpireInMasterSlave() throws IOException {
        RedisServer master = RedisServer.newRedisServer();
        RedisServer slave = RedisServer.newRedisServer();
        master.setSlave(slave);
        master.start();
        slave.start();
        Jedis jedis1 = new Jedis(master.getHost(), master.getBindPort());
        Jedis jedis2 = new Jedis(slave.getHost(), slave.getBindPort());
        assertEquals(jedis1.set("a", "b"), "OK");
        assertEquals(jedis1.expire("a", 1), (Long) 1L);
        assertEquals(jedis2.ttl("a"), (Long) 1L);
        jedis1.disconnect();
        jedis2.disconnect();
        master.stop();
        slave.stop();
    }

    @Test
    public void testExpireAtInMasterSlave() throws IOException {
        RedisServer master = RedisServer.newRedisServer();
        RedisServer slave = RedisServer.newRedisServer();
        master.setSlave(slave);
        master.start();
        slave.start();
        Jedis jedis1 = new Jedis(master.getHost(), master.getBindPort());
        Jedis jedis2 = new Jedis(slave.getHost(), slave.getBindPort());
        assertEquals(jedis1.set("a", "b"), "OK");
        long now = System.currentTimeMillis() / 1000;
        assertEquals(jedis1.expireAt("a", now + 5), (Long) 1L);
        assertEquals(jedis2.ttl("a"), (Long) 5L);
        jedis1.disconnect();
        jedis2.disconnect();
        master.stop();
        slave.stop();
    }


    @Test
    public void testCloseSocket() throws IOException {
        RedisServer server = RedisServer.newRedisServer();
        ServiceOptions options = ServiceOptions.create(3);
        server.setOptions(options);
        server.start();
        Jedis jedis = new Jedis(server.getHost(), server.getBindPort());
        assertEquals(jedis.set("ab", "cd"), "OK");
        assertEquals(jedis.set("ab", "cd"), "OK");
        assertEquals(jedis.set("ab", "cd"), "OK");
        try {
            assertEquals(jedis.set("ab", "cd"), "OK");
            assertTrue(false);
        } catch (JedisConnectionException e) {
            // OK
        }
    }

    @Test
    public void whenRepeatedlyStoppingAndStartingServer_EnsureItResponds() throws IOException {
        for (int i = 0; i < 20; i ++){
            RedisServer server = RedisServer.newRedisServer();
            server.start();

            Jedis jedis = new Jedis(server.getHost(), server.getBindPort());
            assertEquals("PONG", jedis.ping());

            server.stop();
        }
    }
}
