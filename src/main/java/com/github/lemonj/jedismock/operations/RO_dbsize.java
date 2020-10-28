package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_dbsize extends AbstractRedisOperation {
    RO_dbsize(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        return Response.integer(base().keys().size());
    }
}
