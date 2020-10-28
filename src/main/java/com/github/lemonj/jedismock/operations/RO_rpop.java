package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;

import java.util.LinkedList;
import java.util.List;

class RO_rpop extends RO_pop<LinkedList<Slice>> {
    RO_rpop(RedisBase base, List<Slice> params ) {
        super(base, params);
    }

    @Override
    Slice popper(LinkedList<Slice> list) {
        return list.removeLast();
    }
}
