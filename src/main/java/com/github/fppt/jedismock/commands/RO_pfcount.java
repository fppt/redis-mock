package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.deserializeObject;

class RO_pfcount extends AbstractRedisOperation {
    RO_pfcount(RedisBase base, List<Slice> params) {
        super(base, params, null, 0, null);
    }

    Slice response() {
        Set<Slice> set = Sets.newHashSet();
        for (Slice key : params()) {
            Slice data = base().getValue(key);
            if (data == null) {
                continue;
            }

            Set<Slice> s = deserializeObject(data);
            set.addAll(s);
        }
        return Response.integer((long) set.size());
    }
}
