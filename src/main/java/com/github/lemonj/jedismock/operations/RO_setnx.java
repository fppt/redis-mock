package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_setnx extends AbstractRedisOperation {
    RO_setnx(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response(){
        if (base().getValue(params().get(0)) == null) {
            base().putValue(params().get(0), params().get(1));
            return Response.integer(1);
        }
        return Response.integer(0);
    }
}
