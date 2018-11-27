package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_mset extends AbstractRedisOperation {
    RO_mset(RedisBase base, List<Slice> params ) {
        super(base, params, null, 0, 2);
    }

    Slice response() {
        for (int i = 0; i < params().size(); i += 2) {
            base().putValue(params().get(i), params().get(i + 1));
        }
        return Response.OK;
    }
}
