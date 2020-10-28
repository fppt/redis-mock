package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

abstract class RO_incrOrDecrByFloat extends AbstractRedisOperation {
    RO_incrOrDecrByFloat(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    abstract double incrementOrDecrementValue(List<Slice> params);

    Slice response() {
        Slice key = params().get(0);
        double d = incrementOrDecrementValue(params());
        Slice v = base().getValue(key);
        if (v == null) {
            base().putValue(key, Slice.create(String.valueOf(d)));
            return Response.doubleValue(d);
        }

        double r = Utils.convertToDouble(new String(v.data())) + d;
        base().putValue(key, Slice.create(String.valueOf(r)));
        return Response.doubleValue(r);
    }
}
