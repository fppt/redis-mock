package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.List;

import static com.github.fppt.jedismock.Utils.convertToDouble;

class RO_hincrbyfloat extends RO_hincrby {
    RO_hincrbyfloat(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice hsetValue(Slice key1, Slice key2, Slice value) {
        double numericValue = convertToDouble(String.valueOf(value));
        Slice foundValue = base().getValue(key1, key2);
        if (foundValue != null) {
            numericValue = convertToDouble(new String(foundValue.data())) + numericValue;
        }
        Slice res = Slice.create(String.valueOf(numericValue));
        base().putValue(key1, key2, res, -1L);

        return Response.bulkString(res);
    }
}
