package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_hset extends AbstractRedisOperation {
    RO_hset(RedisBase base, List<Slice> params) {
        super(base, params, 3, null, null);
    }

    public RO_hset(RedisBase base, List<Slice> params, Integer expectedParams) {
        super(base, params, expectedParams, null,null);
    }

    Slice response() {
        Slice key1 = params().get(0);
        Slice key2 = params().get(1);
        Slice value = params().get(2);

        Slice oldValue = base().getValue(key1, key2);
        base().putValue(key1, key2, value, -1L);

        if(oldValue == null){
            return Response.integer(1);
        } else {
            return Response.integer(0);
        }
    }
}
