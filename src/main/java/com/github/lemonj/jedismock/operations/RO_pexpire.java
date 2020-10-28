package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_pexpire extends AbstractRedisOperation {
    RO_pexpire(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    long getValue(List<Slice> params){
        return Utils.convertToLong(new String(params.get(1).data()));
    }

    Slice response() {
        return Response.integer(base().setTTL(params().get(0), getValue(params())));
    }
}
