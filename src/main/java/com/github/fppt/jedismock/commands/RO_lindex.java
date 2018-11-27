package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToInteger;
import static com.github.fppt.jedismock.Utils.deserializeObject;

class RO_lindex extends AbstractRedisOperation {
    RO_lindex(RedisBase base, List<Slice> params) {
        super(base, params, 2, null, null);
    }

    Slice response() {
        Slice key = params().get(0);
        Slice data = base().getValue(key);
        LinkedList<Slice> list;
        if (data != null) {
            list = deserializeObject(data);
        } else {
            return Response.NULL;
        }
        int index = convertToInteger(params().get(1).toString());
        if (index < 0) {
            index = list.size() + index;
            if (index < 0) {
                return Response.NULL;
            }
        }
        if (index >= list.size()) {
            return Response.NULL;
        }
        return Response.bulkString(list.get(index));
    }
}
