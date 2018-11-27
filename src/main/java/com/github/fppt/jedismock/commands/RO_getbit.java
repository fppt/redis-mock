package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToNonNegativeInteger;

class RO_getbit extends AbstractRedisOperation {
    RO_getbit(RedisBase base, List<Slice> params) {
        super(base, params, 2, null, null);
    }

    Slice response() {
        Slice value = base().getValue(params().get(0));
        int pos = convertToNonNegativeInteger(params().get(1).toString());

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
