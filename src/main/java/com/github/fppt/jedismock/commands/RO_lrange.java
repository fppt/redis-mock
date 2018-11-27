package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToInteger;
import static com.github.fppt.jedismock.Utils.deserializeObject;

class RO_lrange extends AbstractRedisOperation {
    RO_lrange(RedisBase base, List<Slice> params) {
        super(base, params, 3, null, null);
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

        int start = convertToInteger(params().get(1).toString());
        int end = convertToInteger(params().get(2).toString());

        if (start < 0) {
            start = list.size() + start;
            if (start < 0) {
                start = 0;
            }
        }
        if (end < 0) {
            end = list.size() + end;
            if (end < 0) {
                end = 0;
            }
        }
        ImmutableList.Builder<Slice> builder = new ImmutableList.Builder<Slice>();
        for (int i = start; i <= end && i < list.size(); i++) {
            builder.add(Response.bulkString(list.get(i)));
        }
        return Response.array(builder.build());
    }
}
