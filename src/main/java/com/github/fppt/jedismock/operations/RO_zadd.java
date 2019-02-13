package com.github.fppt.jedismock.operations;


import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.deserializeObject;
import static com.github.fppt.jedismock.Utils.serializeObject;

public class RO_zadd extends AbstractRedisOperation {
    public RO_zadd(RedisBase base, List<Slice> params) {
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
            set = new LinkedHashSet<>();
        }

        for (int i = 1; i < params().size(); i++) {
            set.add(params().get(i));
        }
        try {
            base().putValue(key, serializeObject(set), -1L);
        } catch (Exception e) {
            throw new InternalException(e.getMessage());
        }
        return Response.integer(set.size());
    }
}
