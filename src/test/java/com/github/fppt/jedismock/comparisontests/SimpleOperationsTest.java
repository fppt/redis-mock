package com.github.fppt.jedismock.comparisontests;


import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(Theories.class)
public class SimpleOperationsTest extends ComparisonBase {

    @Theory
    public void whenSettingKeyAndRetrievingIt_CorrectResultIsReturned(Jedis jedis) {
        String key = "key";
        String value = "value";

        assertNull(jedis.get(key));
        jedis.set(key, value);
        assertEquals(value, jedis.get(key));
    }

    @Theory
    public void whenUsingRpop_EnsureTheLastElementPushedIsReturned(Jedis jedis){
        String key = "Another key";
        jedis.rpush(key, "1", "2", "3");
        assertEquals(jedis.rpop(key), "3");
    }

    @Theory
    public void whenUsingRpoplpush_CorrectResultsAreReturned(Jedis jedis){
        String list1key = "list 1";
        String list2key = "list 2";

        jedis.rpush(list1key, "1", "2", "3");
        jedis.rpush(list2key, "a", "b", "c");

        //Check the lists are in order
        List<String> results1 = jedis.lrange(list1key, 0, -1);
        List<String> results2 = jedis.lrange(list2key, 0, -1);

        assertTrue(results1.contains("1"));
        assertTrue(results1.contains("2"));
        assertTrue(results1.contains("3"));

        assertTrue(results2.contains("a"));
        assertTrue(results2.contains("b"));
        assertTrue(results2.contains("c"));

        //Check that the one list has been pushed into the other
        String result = jedis.rpoplpush(list1key, list2key);
        assertEquals("3", result);

        results1 = jedis.lrange(list1key, 0, -1);
        results2 = jedis.lrange(list2key, 0, -1);

        assertTrue(results1.contains("1"));
        assertTrue(results1.contains("2"));
        assertFalse(results1.contains("3"));

        assertTrue(results2.contains("3"));
        assertTrue(results2.contains("a"));
        assertTrue(results2.contains("b"));
        assertTrue(results2.contains("c"));
    }

    @Theory
    public void whenUsingFlushall_EnsureEverythingIsDeleted(Jedis jedis){
        String key = "my-super-special-key";
        String value = "my-not-so-special-value";

        jedis.set(key, value);
        assertEquals(value, jedis.get(key));

        jedis.flushAll();
        assertNull(jedis.get(key));
    }

    @Theory
    public void whenUsingLrem_EnsureDeletionsWorkAsExpected(Jedis jedis){
        String key = "my-super-special-sexy-key";
        String hello = "hello";
        String foo = "foo";

        jedis.rpush(key, hello);
        jedis.rpush(key, hello);
        jedis.rpush(key, foo);
        jedis.rpush(key, hello);

        //Everything in order
        List<String> list = jedis.lrange(key, 0, -1);
        assertEquals(hello, list.get(0));
        assertEquals(hello, list.get(1));
        assertEquals(foo, list.get(2));
        assertEquals(hello, list.get(3));

        long numRemoved = jedis.lrem(key, -2, hello);
        assertEquals(2L, numRemoved);

        //Check order again
        list = jedis.lrange(key, 0, -1);
        assertEquals(hello, list.get(0));
        assertEquals(foo, list.get(1));
    }

    @Theory
    public void whenUsingQuit_EnsureTheConnectionIsClosed(Jedis jedis){
        //Create a new connection
        Client client = jedis.getClient();
        Jedis newJedis = new Jedis(client.getHost(), client.getPort());
        newJedis.set("A happy lucky key", "A sad value");
        assertEquals("OK", newJedis.quit());

        expectedException.expect(JedisConnectionException.class);

        newJedis.set("A happy lucky key", "A sad value 2");
    }

    @Theory
    public void whenConcurrentlyIncrementingAndDecrementingCount_EnsureFinalCountIsCorrect(Jedis jedis) throws ExecutionException, InterruptedException {
        String key = "my-count-tracker";
        int [] count = new int[]{1, 5, 6, 2, -9, -2, 10, 11, 5, -2, -2};

        jedis.set(key, "0");
        assertEquals(0, Integer.parseInt(jedis.get(key)));

        //Increase counts concurrently
        ExecutorService pool = Executors.newCachedThreadPool();
        Set<Future> futues = new HashSet<>();
        for (int i : count) {
            futues.add(pool.submit(() -> {
                Jedis client = new Jedis(jedis.getClient().getHost(), jedis.getClient().getPort());
                client.incrBy(key, i);
                client.close();
            }));
        }


        for (Future futue : futues) {
            futue.get();
        }

        //Check final count
        assertEquals(25, Integer.parseInt(jedis.get(key)));
    }

    @Theory
    public void whenPinging_Pong(Jedis jedis){
        assertEquals("PONG", jedis.ping());
    }

    @Theory
    public void whenGettingKeys_EnsureCorrectKeysAreReturned(Jedis jedis){
        jedis.flushAll();
        jedis.mset("one", "1", "two", "2", "three", "3", "four", "4");

        //Check simple pattern
        Set<String> results = jedis.keys("*o*");
        assertEquals(3, results.size());
        assertTrue(results.contains("one") && results.contains("two") && results.contains("four"));

        //Another simple regex
        results = jedis.keys("t??");
        assertEquals(1, results.size());
        assertTrue(results.contains("two"));

        //All Keys
        results = jedis.keys("*");
        assertEquals(4, results.size());
        assertTrue(results.contains("one") && results.contains("two") && results.contains("three") && results.contains("four"));
    }

    @Theory
    public void whenAddingToASet_EnsureTheSetIsUpdated(Jedis jedis){
        String key = "my-set-key";
        Set<String> mySet = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        //Add everything from the set
        mySet.forEach(value -> jedis.sadd(key, value));

        //Get it all back
        assertEquals(mySet, jedis.smembers(key));
    }

    @Theory
    public void whenPoppingFromASet_EnsureTheSetIsUpdated(Jedis jedis){
        String key = "my-set-key";
        Set<String> mySet = new HashSet<>(Arrays.asList("a", "b", "c", "d"));

        //Add everything from the set
        mySet.forEach(value -> jedis.sadd(key, value));

        String poppedValue;
        do {
            poppedValue = jedis.spop(key);
            if(poppedValue != null) assertTrue("Popped value not in set", mySet.contains(poppedValue));
        } while (poppedValue != null);
    }

    @Theory
    public void whenHSettingOnTheSameKeys_EnsureReturnTypeIs1WhenKeysAreNew(Jedis jedis){
        String field = "my-field";
        String hash = "my-hash";
        assertEquals(new Long(1L), jedis.hset(hash, field, "some value"));
        assertEquals(new Long(0L), jedis.hset(hash, field, "some other value"));
    }

    @Theory
    public void whenHSettingAndHGetting_EnsureValuesAreSetAndRetreived(Jedis jedis){
        String field = "my-field";
        String hash = "my-hash";
        String value = "my-value";

        assertNull(jedis.hget(hash, field));
        jedis.hset(hash, field, value);
        assertEquals(value, jedis.hget(hash, field));
    }

    @Theory
    public void whenHDeleting_EnsureValuesAreRemoved(Jedis jedis){
        String field = "my-field-2";
        String hash = "my-hash-2";
        String value = "my-value-2";

        assertEquals(new Long(0L), jedis.hdel(hash, field));
        jedis.hset(hash, field, value);
        assertEquals(value, jedis.hget(hash, field));
        assertEquals(new Long(1L), jedis.hdel(hash, field));
        assertNull(jedis.hget(hash, field));
    }
}
