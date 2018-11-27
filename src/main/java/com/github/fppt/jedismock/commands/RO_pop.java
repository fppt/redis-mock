package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.deserializeObject;
import static com.github.fppt.jedismock.Utils.serializeObject;

abstract class RO_pop extends AbstractRedisOperation {
    RO_pop(RedisBase base, List<Slice> params ) {
        super(base, params, 1, null, null);
    }

    abstract Slice popper(LinkedList<Slice> list);

    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        LinkedList<Slice> list;
        if (data != null) {
            list = deserializeObject(data);
        } else {
            return Response.NULL;
        }

        if (list.isEmpty()) {
            return Response.NULL;
        }
        Slice v = popper(list);
        base().putValue(key, serializeObject(list));
        return Response.bulkString(v);
    }
}
