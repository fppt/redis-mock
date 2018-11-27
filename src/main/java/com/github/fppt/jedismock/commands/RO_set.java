package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_set extends AbstractRedisOperation {
    RO_set(RedisBase base, List<Slice> params) {
        super(base, params, 2, null, null);
    }

    public RO_set(RedisBase base, List<Slice> params, Integer expectedParams) {
        super(base, params, expectedParams, null,null);
    }

    Slice response() {
        base().putValue(params().get(0), params().get(1));
        return Response.OK;
    }
}
