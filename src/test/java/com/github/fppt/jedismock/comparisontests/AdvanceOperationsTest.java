package com.github.fppt.jedismock.comparisontests;


import com.github.fppt.jedismock.util.MockSubscriber;
import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Theories.class)
public class AdvanceOperationsTest extends ComparisonBase {

    @Theory
    public void whenTransactionWithMultiplePushesIsExecuted_EnsureResultsAreSaved(Jedis jedis) {
        String key = "my-list";
        assertEquals(new Long(0), jedis.llen(key));

        Transaction transaction = jedis.multi();
        transaction.lpush(key, "1");
        transaction.lpush(key, "2");
        transaction.lpush(key, "3");
        transaction.exec();

        assertEquals(new Long(3), jedis.llen(key));
    }

    @Theory
    public void whenUsingTransactionAndTryingToAccessJedis_Throw(Jedis jedis) {
        //Do Something random with Jedis
        assertNull(jedis.get("oobity-oobity-boo"));

        //Start transaction
        Transaction transaction = jedis.multi();

        expectedException.expect(JedisDataException.class);
        expectedException.expectMessage("Cannot use Jedis when in Multi. Please use Transation or reset jedis state.");

        jedis.get("oobity-oobity-boo");
    }

    @Theory
    public void whenSubscribingToAChannel_EnsurePublishedMessagesAreReceived(Jedis jedis) throws InterruptedException {
        String channel = "normalbob";
        String message = "SUPERBOB";

        //Create subscriber
        ExecutorService subsciberThread = Executors.newSingleThreadExecutor();
        MockSubscriber mockSubscriber = new MockSubscriber();

        Client client = jedis.getClient();
        Jedis subscriber = new Jedis(client.getHost(), client.getPort());

        subsciberThread.submit(() -> subscriber.subscribe(mockSubscriber, channel));

        //Give some time to subscribe
        Thread.sleep(50);

        //publish message
        jedis.publish(channel, message);

        //Give some time for the message to go through
        Thread.sleep(50);

        assertEquals(channel, mockSubscriber.latestChannel());
        assertEquals(message, mockSubscriber.latestMessage());

        mockSubscriber.unsubscribe();
        subsciberThread.shutdownNow();
    }

    @Theory
    public void whenUsingBrpoplpush_EnsureItBlocksAndCorrectResultsAreReturned(Jedis jedis) throws ExecutionException, InterruptedException {
        String list1key = "source list";
        String list2key = "target list";

        jedis.rpush(list2key, "a", "b", "c");

        //Block on performing the BRPOPLPUSH
        Client client = jedis.getClient();
        Jedis blockedClient = new Jedis(client.getHost(), client.getPort());
        ExecutorService blockingThread = Executors.newSingleThreadExecutor();
        Future future = blockingThread.submit(() -> {
            String result = blockedClient.brpoplpush(list1key, list2key, 500);
            assertEquals("3", result);
        });

        //Check the list is not modified
        List<String> results = jedis.lrange(list2key, 0, -1);
        assertEquals(3, results.size());

        //Push some stuff into the blocked list
        jedis.rpush(list1key, "1", "2", "3");

        future.get();

        //Check the list is modified
        results = jedis.lrange(list2key, 0, -1);
        assertEquals(4, results.size());
    }

    @Theory
    public void whenUsingBrpoplpushAndReachingTimeout_Return(Jedis jedis){
        String list1key = "another source list";
        String list2key = "another target list";

        String result = jedis.brpoplpush(list1key, list2key, 1);

        assertNull(result);
    }

    @Theory
    public void whenUsingBrpoplpush_EnsureClientCanStillGetOtherResponsesInTimelyManner(Jedis jedis){
        String list1key = "another another source list";
        String list2key = "another another target list";

        Client client = jedis.getClient();
        Jedis blockedClient = new Jedis(client.getHost(), client.getPort());
        ExecutorService blockingThread = Executors.newSingleThreadExecutor();
        blockingThread.submit(() -> {
            String result = blockedClient.brpoplpush(list1key, list2key, 500);
            assertEquals("3", result);
        });

        //Issue random commands to make sure mock is still responsive
        jedis.set("k1", "v1");
        jedis.set("k2", "v2");
        jedis.set("k3", "v3");
        jedis.set("k4", "v4");
        jedis.set("k5", "v5");

        //Check random commands were processed
        assertEquals("v1", jedis.get("k1"));
        assertEquals("v2", jedis.get("k2"));
        assertEquals("v3", jedis.get("k3"));
        assertEquals("v4", jedis.get("k4"));
        assertEquals("v5", jedis.get("k5"));
    }

    @Ignore("This test fails with the embedded redis sometimes so I am at a loss")
    @Theory
    public void whenSubscribingToAChannelAndThenUnsubscribing_EnsureAllChannelsAreUbSubScribed(Jedis jedis) throws InterruptedException, ExecutionException {
        String channel1 = "normaltim1";
        String channel2 = "normaltim2";
        //String channel3 = "normaltim3";
        String message = "SUPERTIM";

        //Create Subscriber
        ExecutorService subsciberThread = Executors.newCachedThreadPool();
        Client client = jedis.getClient();
        Jedis subscriber = new Jedis(client.getHost(), client.getPort());

        subscriber.set("thing", "a-thing");
        assertEquals("a-thing", subscriber.get("thing"));

        //Subscribe to Channels
        subsciberThread.submit(() -> subscribeToChannel(subscriber, channel1));
        subsciberThread.submit(() -> subscribeToChannel(subscriber, channel2));
        //subsciberThread.submit(() -> subscribeToChannel(subscriber, channel3));

        //Give some time to subscribe
        Thread.sleep(5000);

        //publish messages which trigger the unsubcription
        jedis.publish(channel1, message);
        jedis.publish(channel2, message);
        //jedis.publish(channel3, message);

        //Give some time for publications to go through
        Thread.sleep(2000);

        subscriber.set("thing", "a-new-thing");
    }

    private static void subscribeToChannel(Jedis subscriber, String channel){
        System.out.println("Subscribing to :" + channel);
        subscriber.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println("Unsubscribing from :" + channel);
                unsubscribe(channel);
            }
        }, channel);
    }

    @Theory
    public void whenChangingBetweenRedisDBS_EnsureChangesAreMutuallyExclusive(Jedis jedis){
        String key1 = "k1";
        String key2 = "k2";

        String val1 = "v1";
        String val2 = "v2";
        String val3 = "v3";

        //Mess With Default Cluster
        jedis.set(key1, val1);
        jedis.set(key2, val2);
        assertEquals(val1, jedis.get(key1));
        assertEquals(val2, jedis.get(key2));

        //Change to new DB
        jedis.select(2);
        assertNull(jedis.get(key1));
        assertNull(jedis.get(key2));

        jedis.set(key1, val3);
        jedis.set(key2, val3);
        assertEquals(val3, jedis.get(key1));
        assertEquals(val3, jedis.get(key2));

        //Change back and make sure original is unchanged
        jedis.select(0);
        assertEquals(val1, jedis.get(key1));
        assertEquals(val2, jedis.get(key2));
    }

}
