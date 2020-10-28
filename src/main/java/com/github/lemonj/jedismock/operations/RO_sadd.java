package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RO_sadd extends AbstractRedisOperation {
    RO_sadd(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        Set<Slice> set = getDataFromBase(key, new HashSet<>());

        int count = 0;
        for (int i = 1; i < params().size(); i++) {
            if (set.add(params().get(i))){
                count++;
            }
        }
        try {
            base().putValue(key, Utils.serializeObject(set));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Response.integer(count);
    }
}
