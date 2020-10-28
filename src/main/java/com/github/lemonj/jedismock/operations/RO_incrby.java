package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_incrby extends RO_incrOrDecrBy {
    RO_incrby(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    long incrementOrDecrementValue(List<Slice> params){
        return Utils.convertToLong(String.valueOf(params.get(1)));
    }
}
