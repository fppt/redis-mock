package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;
import java.util.UUID;

/**
 * @Author: lmj
 * @Description:
 * @Date: Create in 10:30 上午 2020/12/29
 **/
class RO_script extends AbstractRedisOperation {

    RO_script(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        List<Slice> params = params();

        if ("LOAD".equals(params.get(0).toString())) {
            Slice scriptKey = Slice.create(UUID.randomUUID().toString());
            base().putValue(scriptKey, params.get(1));

            return Response.bulkString(scriptKey);
        }

        return Response.OK;
    }
}
