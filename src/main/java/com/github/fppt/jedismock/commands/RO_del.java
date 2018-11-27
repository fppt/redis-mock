package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_del extends AbstractRedisOperation {
    RO_del(RedisBase base, List<Slice> params) {
        super(base, params,null, 0, null);
    }

    Slice response(){
        int count = 0;
        for (Slice key : params()) {
            Slice value = base().getValue(key);
            base().deleteValue(key);
            if (value != null) {
                count++;
            }
        }
        return Response.integer(count);
    }
}
