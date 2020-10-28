package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;
import java.util.Map;

public class RO_hlen extends AbstractRedisOperation {
    public RO_hlen(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Slice key = params().get(0);
        Map<Slice, Slice> map = base().getFieldsAndValues(key);
        return Response.integer(map.size());
    }
}
