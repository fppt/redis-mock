package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.LinkedList;
import java.util.List;

public class RO_brpop extends RO_blpop {

    public RO_brpop(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice popper(LinkedList<Slice> list) {
        return list.removeLast();
    }
}
