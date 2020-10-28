package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_del extends AbstractRedisOperation {
    RO_del(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        int count = 0;
        for (Slice key : params()) {
            if (base().exists(key)) {
                base().deleteValue(key);
                count++;
            }
        }
        return Response.integer(count);
    }
}
