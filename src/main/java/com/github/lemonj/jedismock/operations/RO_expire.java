package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;

import java.util.List;

class RO_expire extends RO_pexpire {
    RO_expire(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    long getValue(List<Slice> params){
        return super.getValue(params) * 1000;
    }
}
