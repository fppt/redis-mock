package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;


public class RO_hexists extends AbstractRedisOperation {
    RO_hexists(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        if (base().getValue(params().get(0), params().get(1)) == null){
            return Response.integer(0);
        }
        return Response.integer(1);
    }
}