package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_exists extends AbstractRedisOperation {
    RO_exists(RedisBase base, List<Slice> params) {
        super(base, params, 1, null, null);
    }

    Slice response() {
        if (base().getValue(params().get(0)) != null) {
            return Response.integer(1);
        }
        return Response.integer(0);
    }
}
