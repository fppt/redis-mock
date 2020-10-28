package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_psetex extends RO_setex {
    RO_psetex(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    long valueToSet(List<Slice> params){
        return Utils.convertToLong(new String(params.get(1).data()));
    }
}
