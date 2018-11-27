package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.deserializeObject;
import static com.github.fppt.jedismock.Utils.serializeObject;

class RO_spop extends AbstractRedisOperation {
    RO_spop(RedisBase base, List<Slice> params ) {
        super(base, params, 1, null, null);
    }

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
        Slice v = it.next();
        it.remove();
        base().putValue(key, serializeObject(set));
        return Response.bulkString(v);
    }
}
