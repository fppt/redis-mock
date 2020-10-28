package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

public class RO_hmset extends AbstractRedisOperation {
    public RO_hmset(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice hash = params().get(0);

        for(int i = 1; i < params().size(); i = i + 2){
            Slice field = params().get(i);
            Slice value = params().get(i+1);
            base().putValue(hash, field, value, -1L);
        }

        return Response.OK;
    }
}
