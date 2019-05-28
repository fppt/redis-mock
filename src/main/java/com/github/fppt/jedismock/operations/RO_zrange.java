package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.fppt.jedismock.Utils.convertToInteger;

class RO_zrange extends AbstractRedisOperation {

    RO_zrange(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        Map<Slice, Slice> map = getDataFromBase(key, new LinkedHashMap<>());

        int start = convertToInteger(params().get(1).toString());
        int end = convertToInteger(params().get(2).toString());

        if (start < 0) {
            start = map.size() + start;
            if (start < 0) {
                start = 0;
            }
        }

        if (end < 0) {
            end = map.size() + end;
            if (end < 0) {
                end = -1;
            }
        }

        if (end >= map.size()) {
            end = map.size() - 1;
        }

        List<Slice> values = map.keySet().stream()
            .skip(start)
            .limit(end - start + 1)
            .map(Response::bulkString)
            .collect(Collectors.toList());

        return Response.array(values);
    }
}
