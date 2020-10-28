package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_blpop extends RO_bpop {
    RO_blpop(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    RO_pop popper(List<Slice> params) {
        return new RO_lpop(base(), params);
    }
}
