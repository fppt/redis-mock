package com.github.fppt.jedismock.storage;

import com.github.fppt.jedismock.Slice;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


/**
 * Used to represent an expiring storage layer.
 */
@AutoValue
public abstract class ExpiringKeyValueStorage {
    public abstract Table<Slice, Slice, Slice> values();
    public abstract Table<Slice, Slice, Long> ttls();

    public static ExpiringKeyValueStorage create(){
        return new AutoValue_ExpiringKeyValueStorage(HashBasedTable.create(), HashBasedTable.create());
    }

    public void delete(Slice key){
        delete(key, Slice.reserved());
    }

    public void delete(Slice key1, Slice key2){
        Preconditions.checkNotNull(key1);
        Preconditions.checkNotNull(key2);
        values().remove(key1, key2);
        ttls().remove(key1, key2);
    }

    public void clear(){
        values().clear();
        ttls().clear();
    }

    public Slice get(Slice key){
        return get(key, Slice.reserved());
    }

    public Slice get(Slice key1, Slice key2){
        Preconditions.checkNotNull(key1);
        Preconditions.checkNotNull(key2);

        Long deadline = ttls().get(key1, key2);
        if (deadline != null && deadline != -1 && deadline <= System.currentTimeMillis()) {
            delete(key1, key2);
            return null;
        }
        return values().get(key1, key2);
    }

    public Long getTTL(Slice key){
        return getTTL(key, Slice.reserved());
    }

    public Long getTTL(Slice key1, Slice key2){
        Preconditions.checkNotNull(key1);
        Preconditions.checkNotNull(key2);

        Long deadline = ttls().get(key1, key2);
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
        delete(key1, key1);
        return null;
    }

    public long setTTL(Slice key, long ttl){
        return setTTL(key, Slice.reserved(), ttl);
    }

    public long setTTL(Slice key1, Slice key2, long ttl){
        return setDeadline(key1, key2, ttl + System.currentTimeMillis());
    }

    public void put(Slice key, Slice value, Long ttl){
        put(key, Slice.reserved(), value, ttl);
    }

    public void put(Slice key1, Slice key2, Slice value, Long ttl){
        Preconditions.checkNotNull(key1);
        Preconditions.checkNotNull(key2);
        Preconditions.checkNotNull(value);

        values().put(key1, key2, value);
        if (ttl != null) {
            if (ttl != -1) {
                setTTL(key1, key2, ttl);
            } else {
                setDeadline(key1, key2, -1L);
            }
        }
    }

    public long setDeadline(Slice key, long deadline) {
        return setDeadline(key, Slice.reserved(), deadline);
    }

    public long setDeadline(Slice key1, Slice key2, long deadline) {
        Preconditions.checkNotNull(key1);
        Preconditions.checkNotNull(key2);

        if (values().contains(key1, key2)) {
            ttls().put(key1, key2, deadline);
            return 1L;
        }
        return 0L;
    }
}
