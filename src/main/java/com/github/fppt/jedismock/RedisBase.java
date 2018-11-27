package com.github.fppt.jedismock;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisBase {
    private final Map<Slice, Set<RedisClient>> subscribers = new ConcurrentHashMap<>();
    private final Hashtable<Slice, Slice> base = new Hashtable<>();
    private final Hashtable<Slice, Long> deadlines = new Hashtable<>();
    private final Set<RedisBase> syncBases = ConcurrentHashMap.newKeySet();

    public RedisBase() {}

    public void addSyncBase(RedisBase base) {
        syncBases.add(base);
    }

    public Set<Slice> keys(){
        return base.keySet();
    }

    public Slice rawGet(Slice key) {
        Preconditions.checkNotNull(key);

        Long deadline = deadlines.get(key);
        if (deadline != null && deadline != -1 && deadline <= System.currentTimeMillis()) {
            base.remove(key);
            deadlines.remove(key);
            return null;
        }
        return base.get(key);
    }

    public Long getTTL(Slice key) {
        Preconditions.checkNotNull(key);

        Long deadline = deadlines.get(key);
        if (deadline == null) {
            return null;
        }
        if (deadline == -1) {
            return deadline;
        }
        long now = System.currentTimeMillis();
        if (now < deadline) {
            return deadline - now;
        }
        base.remove(key);
        deadlines.remove(key);
        return null;
    }

    public long setTTL(Slice key, long ttl) {
        Preconditions.checkNotNull(key);

        if (base.containsKey(key)) {
            deadlines.put(key, ttl + System.currentTimeMillis());
            for (RedisBase base : syncBases) {
                base.setTTL(key, ttl);
            }
            return 1L;
        }
        return 0L;
    }

    public long setDeadline(Slice key, long deadline) {
        Preconditions.checkNotNull(key);

        if (base.containsKey(key)) {
            deadlines.put(key, deadline);
            for (RedisBase base : syncBases) {
                base.setDeadline(key, deadline);
            }
            return 1L;
        }
        return 0L;
    }

    public void clear(){
        base.clear();
        subscribers.clear();
        deadlines.clear();
        syncBases.clear();
    }

    public void rawPut(Slice key, Slice value, Long ttl) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        base.put(key, value);
        if (ttl != null) {
            if (ttl != -1) {
                deadlines.put(key, ttl + System.currentTimeMillis());
            } else {
                deadlines.put(key, -1L);
            }
        }
        for (RedisBase base : syncBases) {
            base.rawPut(key, value, ttl);
        }
    }

    public void del(Slice key) {
        Preconditions.checkNotNull(key);

        base.remove(key);
        deadlines.remove(key);

        for (RedisBase base : syncBases) {
            base.del(key);
        }
    }

    public void addSubscriber(Slice channel, RedisClient client){
        Set<RedisClient> newClient = new HashSet<>();
        newClient.add(client);
        subscribers.merge(channel, newClient, (currentSubscribers, newSubscribers) -> {
            currentSubscribers.addAll(newSubscribers);
            return currentSubscribers;
        });
    }

    public boolean removeSubscriber(Slice channel, RedisClient client){
        if(subscribers.containsKey(channel)){
            subscribers.get(channel).remove(client);
            return true;
        }
        return false;
    }

    public Set<RedisClient> getSubscribers(Slice channel){
        if (subscribers.containsKey(channel)) {
            return subscribers.get(channel);
        }
        return Collections.emptySet();
    }

    public List<Slice> getSubscriptions(RedisClient client){
        List<Slice> subscriptions = new ArrayList<>();

        subscribers.forEach((channel, subscribers) -> {
            if(subscribers.contains(client)){
                subscriptions.add(channel);
            }
        });

        return subscriptions;
    }
}
