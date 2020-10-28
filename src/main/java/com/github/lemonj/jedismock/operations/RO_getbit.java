package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.List;

class RO_getbit extends AbstractRedisOperation {
    RO_getbit(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Slice value = base().getValue(params().get(0));
        int pos = Utils.convertToNonNegativeInteger(params().get(1).toString());

        if (value == null) {
            return Response.integer(0L);
        }
        if (pos >= value.length() * 8) {
            return Response.integer(0L);
        }
        if ((value.data()[pos / 8] & (1 << (pos % 8))) != 0) {
            return Response.integer(1);
        }
        return Response.integer(0);
    }
}
