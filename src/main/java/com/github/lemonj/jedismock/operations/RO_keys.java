package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.Utils;
import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.ArrayList;
import java.util.List;

class RO_keys extends AbstractRedisOperation {
    RO_keys(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    Slice response() {
        List<Slice> matchingKeys = new ArrayList<>();
        String regex = Utils.createRegexFromGlob(new String(params().get(0).data()));

        base().keys().forEach(keyData -> {
            String key = new String(keyData.data());
            if(key.matches(regex)){
                matchingKeys.add(Response.bulkString(keyData));
            }
        });

        return Response.array(matchingKeys);
    }
}
