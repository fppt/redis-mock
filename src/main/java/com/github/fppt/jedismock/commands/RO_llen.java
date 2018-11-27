package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.deserializeObject;

class RO_llen extends AbstractRedisOperation {
    RO_llen(RedisBase base, List<Slice> params) {
        super(base, params,  1, null, null);
    }

    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        LinkedList<Slice> list;
        if (data != null) {
            list = deserializeObject(data);
        } else {
            list = Lists.newLinkedList();
        }
        return Response.integer(list.size());
    }
}
