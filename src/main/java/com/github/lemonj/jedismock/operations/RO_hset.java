package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_hset extends AbstractRedisOperation {
    RO_hset(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice hsetValue(Slice key1, Slice key2, Slice value){
        Slice foundValue = base().getValue(key1, key2);
        base().putValue(key1, key2, value, -1L);
        return foundValue;
    }

    Slice response() {
        Slice key1 = params().get(0);
        Slice key2 = params().get(1);
        Slice value = params().get(2);
        Slice oldValue = hsetValue(key1, key2, value);

        if(oldValue == null){
            return Response.integer(1);
        } else {
            return Response.integer(0);
        }
    }
}
