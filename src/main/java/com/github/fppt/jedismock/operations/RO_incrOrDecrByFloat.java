package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToDouble;

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

        double r = convertToDouble(new String(v.data())) + d;
        base().putValue(key, Slice.create(String.valueOf(r)));
        return Response.doubleValue(r);
    }
}
