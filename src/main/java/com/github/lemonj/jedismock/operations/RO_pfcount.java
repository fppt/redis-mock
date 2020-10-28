package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

class RO_pfcount extends AbstractRedisOperation {
    RO_pfcount(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Set<Slice> set = Sets.newHashSet();
        for (Slice key : params()) {
            Slice data = base().getValue(key);
            if (data == null) {
                continue;
            }

            Set<Slice> s = Utils.deserializeObject(data);
            set.addAll(s);
        }
        return Response.integer((long) set.size());
    }
}
