package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_strlen extends AbstractRedisOperation {
    RO_strlen(RedisBase base, List<Slice> params) {
        super(base, params, 1, null, null);
    }

    Slice response() {
        Slice value = base().getValue(params().get(0));
        if (value == null) {
            return Response.integer(0);
        }
        return Response.integer(value.length());
    }
}
