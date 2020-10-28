package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;

class RO_brpop extends RO_bpop {

    RO_brpop(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    RO_pop popper(List<Slice> params) {
        return new RO_rpop(base(), params);
    }
}
