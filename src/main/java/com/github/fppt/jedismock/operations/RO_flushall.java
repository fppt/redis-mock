package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.List;

class RO_flushall extends AbstractRedisOperation {
    RO_flushall(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response(){
        base().clear();
        return Response.OK;
    }
}
