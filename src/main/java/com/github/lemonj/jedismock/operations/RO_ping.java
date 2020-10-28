package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_ping extends AbstractRedisOperation {
    RO_ping(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        if (params().isEmpty()){
            return Response.bulkString(Slice.create("PONG"));
        }

        return Response.bulkString(params().get(0));
    }
}
