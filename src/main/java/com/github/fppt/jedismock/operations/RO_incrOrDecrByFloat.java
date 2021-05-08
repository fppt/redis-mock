package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.math.BigDecimal;
import java.util.List;

abstract class RO_incrOrDecrByFloat extends AbstractRedisOperation {
    RO_incrOrDecrByFloat(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Slice key = params().get(0);
        BigDecimal numericValue = new BigDecimal(params().get(1).toString());

        Slice foundValue = base().getValue(key);
        if (foundValue != null) {
            numericValue = numericValue.add(new BigDecimal((new String(foundValue.data()))));
        }

        String data = String.valueOf(BigDecimal.valueOf(numericValue.intValue()).compareTo(numericValue) == 0
                ? numericValue.intValue() : numericValue);

        Slice res = Slice.create(data);
        base().putValue(key, res);

        return Response.bulkString(res);
    }
}
