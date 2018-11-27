package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.deserializeObject;
import static com.github.fppt.jedismock.Utils.serializeObject;

abstract class RO_push extends AbstractRedisOperation {
    RO_push(RedisBase base, List<Slice> params) {
        super(base, params,null, 1, null);
    }

    abstract void pusher(LinkedList<Slice> list, Slice slice);

    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        LinkedList<Slice> list;

        if (data != null) {
            list = deserializeObject(data);
        } else {
            list = Lists.newLinkedList();
        }

        for (int i = 1; i < params().size(); i++) {
            pusher(list, params().get(i));
        }
        try {
            base().putValue(key, serializeObject(list));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Response.integer(list.size());
    }
}
