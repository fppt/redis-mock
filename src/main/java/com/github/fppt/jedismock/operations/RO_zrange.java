package com.github.fppt.jedismock.operations;


import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.convertToInteger;
import static com.github.fppt.jedismock.Utils.deserializeObject;

public class RO_zrange extends AbstractRedisOperation {
    public RO_zrange(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        Set<Slice> set;
        if (data != null) {
            set = deserializeObject(data);
        } else {
            set = Sets.newLinkedHashSet();
        }

        int start = convertToInteger(params().get(1).toString());
        int end = convertToInteger(params().get(2).toString());

        if (start < 0) {
            start = set.size() + start;
            if (start < 0) {
                start = 0;
            }
        }
        if (end < 0) {
            end = set.size() + end;
            if (end < 0) {
                end = 0;
            }
        }
        ImmutableList.Builder<Slice> builder = new ImmutableList.Builder<Slice>();
        Iterator<Slice> iterator = set.iterator();
        int i = 0;
        while (iterator.hasNext() && i <= end) {
            if (i >= start) {
                builder.add(Response.bulkString(iterator.next()));
            }
            i++;
        }
        return Response.array(builder.build());
    }
}
