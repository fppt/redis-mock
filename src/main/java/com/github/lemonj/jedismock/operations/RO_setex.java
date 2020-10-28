package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_setex extends RO_set {
    RO_setex(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    long valueToSet(List<Slice> params){
        return Utils.convertToLong(new String(params.get(1).data())) * 1000;
    }

    Slice response() {
        base().putValue(params().get(0), params().get(2), valueToSet(params()));
        return Response.OK;
    }
}
