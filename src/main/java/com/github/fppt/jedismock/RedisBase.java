package com.github.fppt.jedismock;

import com.github.fppt.jedismock.storage.ExpiringKeyValueStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisBase {
    private final ExpiringKeyValueStorage keyValueStorage = ExpiringKeyValueStorage.create();
    private final Map<Slice, Set<RedisClient>> subscribers = new ConcurrentHashMap<>();
    private final Set<RedisBase> syncBases = ConcurrentHashMap.newKeySet();

    public RedisBase() {}

    public void addSyncBase(RedisBase base) {
        syncBases.add(base);
    }


    public Set<Slice> keys(){
        return keyValueStorage.values().rowMap().keySet();
    }

    public Slice getValue(Slice key) {
        return keyValueStorage.get(key);
    }

    public Slice getValue(Slice key1, Slice key2) {
        return keyValueStorage.get(key1, key2);
    }

    public Long getTTL(Slice key) {
        return keyValueStorage.getTTL(key);
    }

    private void syncBases(Consumer<RedisBase> syncFunction){
        for (RedisBase base : syncBases) {
            syncFunction.accept(base);
        }
    }

    public long setTTL(Slice key, long ttl) {
        long result = keyValueStorage.setTTL(key, ttl);
        syncBases((base) -> base.setTTL(key, ttl));
        return result;
    }

    public long setDeadline(Slice key, long deadline) {
        long result = keyValueStorage.setDeadline(key, deadline);
        syncBases((base) -> base.setDeadline(key, deadline));
        return result;
    }

    public void clear(){
        keyValueStorage.clear();
        subscribers.clear();
        syncBases.clear();
    }

    public void putValue(Slice key, Slice value){
        putValue(key, value, -1L);
    }

    public void putValue(Slice key, Slice value, Long ttl) {
        keyValueStorage.put(key, value, ttl);
        syncBases((base) -> base.putValue(key, value, ttl));
    }

    public void putValue(Slice key1, Slice key2, Slice value, Long ttl) {
        keyValueStorage.put(key1, key2, value, ttl);
        syncBases((base) -> base.putValue(key1, key2, value, ttl));
    }

    public void deleteValue(Slice key) {
        keyValueStorage.delete(key);
        syncBases((base) -> base.deleteValue(key));
    }

    public void deleteValue(Slice key1, Slice key2) {
        keyValueStorage.delete(key1, key2);
        syncBases((base) -> base.deleteValue(key1, key2));
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
