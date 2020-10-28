package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.server.SliceParser;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RO_rpoplpush extends AbstractRedisOperation {
    RO_rpoplpush(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        Slice source = params().get(0);
        Slice target = params().get(1);

        //Pop last one
        Slice result = new RO_rpop(base(), Collections.singletonList(source)).execute();
        if(result.equals(Response.NULL)) return Response.NULL;

        Slice valueToPush = SliceParser.consumeParameter(result.data());

        //Push it into the other list
        new RO_lpush(base(), Arrays.asList(target, valueToPush)).execute();

        return result;
    }
}
