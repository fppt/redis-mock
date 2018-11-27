package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.ImmutableList;

import java.util.List;

class RO_mget extends AbstractRedisOperation {
    RO_mget(RedisBase base, List<Slice> params) {
        super(base, params, null, 0, null);
    }

    Slice response(){
        ImmutableList.Builder<Slice> builder = new ImmutableList.Builder<Slice>();
        for (Slice key : params()) {
            builder.add(Response.bulkString(base().getValue(key)));

        }
        return Response.array(builder.build());
    }
}
