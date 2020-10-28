package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

abstract class RO_incrOrDecrBy extends AbstractRedisOperation {
    RO_incrOrDecrBy(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    abstract long incrementOrDecrementValue(List<Slice> params);

    Slice response() {
        Slice key = params().get(0);
        long d = incrementOrDecrementValue(params());
        Slice v = base().getValue(key);
        if (v == null) {
            base().putValue(key, Slice.create(String.valueOf(d)));
            return Response.integer(d);
        }

        long r = Utils.convertToLong(new String(v.data())) + d;
        base().putValueWithoutClearingTtl(key, Slice.create(String.valueOf(r)));
        return Response.integer(r);
    }
}
