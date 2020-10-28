package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.Collection;
import java.util.List;

abstract class RO_pop<V extends Collection<Slice>> extends AbstractRedisOperation {
    RO_pop(RedisBase base, List<Slice> params ) {
        super(base, params);
    }

    abstract Slice popper(V list);

    Slice response() {
        Slice key = params().get(0);
        V list = getDataFromBase(key, null);
        if(list == null || list.isEmpty()) return Response.NULL;
        Slice v = popper(list);
        base().putValue(key, Utils.serializeObject(list));
        return Response.bulkString(v);
    }
}
