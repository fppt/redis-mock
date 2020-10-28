package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_pexpireat extends AbstractRedisOperation {
    RO_pexpireat(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        long deadline = Utils.convertToLong(new String(params().get(1).data()));
        return Response.integer(base().setDeadline(params().get(0), deadline));
    }
}
