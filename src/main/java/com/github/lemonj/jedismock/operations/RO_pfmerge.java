package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

class RO_pfmerge extends AbstractRedisOperation {
    RO_pfmerge(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        boolean first;

        Set<Slice> set;
        if (data == null) {
            set = Sets.newHashSet();
            first = true;
        } else {
            set = Utils.deserializeObject(data);
            first = false;
        }
        for (Slice v : params().subList(1, params().size())) {
            Slice src = base().getValue(v);
            if (src != null) {
                Set<Slice> s = Utils.deserializeObject(src);
                set.addAll(s);
            }
        }

        Slice out = Utils.serializeObject(set);
        if (first) {
            base().putValue(key, out);
        } else {
            base().putValue(key, out, null);
        }
        return Response.OK;
    }
}
