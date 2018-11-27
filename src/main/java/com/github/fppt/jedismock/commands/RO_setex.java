package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToLong;

class RO_setex extends RO_set {
    RO_setex(RedisBase base, List<Slice> params) {
        super(base, params, 3);
    }

    RO_setex(RedisBase base, List<Slice> params, Integer expectedParams) {
        super(base, params, expectedParams);
    }

    long valueToSet(List<Slice> params){
        return convertToLong(new String(params.get(1).data())) * 1000;
    }

    Slice response() {
        base().putValue(params().get(0), params().get(2), valueToSet(params()));
        return Response.OK;
    }
}
