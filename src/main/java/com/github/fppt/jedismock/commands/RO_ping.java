package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_ping extends AbstractRedisOperation {
    RO_ping(RedisBase base, List<Slice> params) {
        super(base, params,  0, null, null);
    }

    Slice response() {
        if (params().isEmpty()){
            return Response.bulkString(Slice.create("PONG"));
        }

        return Response.bulkString(params().get(0));
    }
}
