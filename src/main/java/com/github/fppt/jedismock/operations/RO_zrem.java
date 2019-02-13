package com.github.fppt.jedismock.operations;


import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.deserializeObject;
import static com.github.fppt.jedismock.Utils.serializeObject;

public class RO_zrem extends AbstractRedisOperation {
    public RO_zrem(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        Set<Slice> set;
        if (data != null) {
            set = deserializeObject(data);
        } else {
            return Response.NULL;
        }

        if (set.isEmpty()) {
            return Response.NULL;
        }
        Iterator<Slice> it = set.iterator();
        it.next();
        it.remove();
        base().putValue(key, serializeObject(set), -1L);
        return Response.integer(1);
    }
}
