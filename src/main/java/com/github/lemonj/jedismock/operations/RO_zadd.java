package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

class RO_zadd extends AbstractRedisOperation {

    RO_zadd(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        LinkedHashMap<Slice, Double> map = getDataFromBase(key, new LinkedHashMap<>());

        int count = 0;
        for (int i = 1; i < params().size(); i += 2) {
            Slice score = params().get(i);
            Slice value = params().get(i + 1);

            // Score must be a double. Will throw an exception if it's not.
            double s = Utils.convertToDouble(score.toString());

            Double prevScore = map.put(value, s);
            if (prevScore == null) count++;
        }

        // Sort the map by value (the score) before saving
        Map<Slice, Double> sortedMap = map.entrySet()
            .stream()
            .map(x -> new AbstractMap.SimpleEntry<>(x.getKey(), Utils.convertToDouble(x.getValue().toString())))
            .sorted(Map.Entry.comparingByValue())
            .collect(
                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        try {
            base().putValue(key, Utils.serializeObject(sortedMap));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Response.integer(count);
    }

}
