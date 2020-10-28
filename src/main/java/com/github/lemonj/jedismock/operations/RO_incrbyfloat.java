package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_incrbyfloat extends RO_incrOrDecrByFloat {
    RO_incrbyfloat(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    double incrementOrDecrementValue(List<Slice> params){
        return Utils.convertToDouble(String.valueOf(params.get(1)));
    }
}
