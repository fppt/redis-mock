package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

class RO_hdel extends AbstractRedisOperation {
    RO_hdel(RedisBase base, List<Slice> params) {
        super(base, params,2, null, null);
    }

    Slice response(){
        Slice key1 = params().get(0);
        Slice key2 = params().get(1);

        Slice oldValue = base().getValue(key1, key2);

        base().deleteValue(key1, key2);

        if(oldValue == null){
            return Response.integer(0);
        } else {
            return Response.integer(1);
        }
    }
}
