package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RO_hegetall extends AbstractRedisOperation {
    public RO_hegetall(RedisBase base, List<Slice> params) {
        super(base, params, 1, null, null);
    }

    @Override
    Slice response() {
        Slice hash = params().get(0);

        Map<Slice, Slice> fieldAndValueMap = base().getFieldsAndValues(hash);
        int arraySize = fieldAndValueMap.size() * 2;
        Slice [] fieldAndValueList = new Slice[arraySize];

        int currentIndex = arraySize - 1;
        for (Map.Entry<Slice, Slice> entry: fieldAndValueMap.entrySet()){
            fieldAndValueList[currentIndex] = Response.bulkString(entry.getValue());
            currentIndex--;

            fieldAndValueList[currentIndex] = Response.bulkString(entry.getKey());
            currentIndex--;
        }

        List<Slice> values = Arrays.asList(fieldAndValueList);
        return Response.array(values);
    }
}
