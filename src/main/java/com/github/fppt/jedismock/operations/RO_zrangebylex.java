package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class RO_zrangebylex extends AbstractRedisOperation {

    private static final String START_UNBOUNDED = "-";
    private static final String END_UNBOUNDED = "+";
    private static final String INCLUSIVE_PREFIX = "[";
    private static final String EXCLUSIVE_PREFIX = "(";

    RO_zrangebylex(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        LinkedHashMap<Slice, Double> map = getDataFromBase(key, new LinkedHashMap<>());

        String start = params().get(1).toString();
        if (!START_UNBOUNDED.equals(start)
                && !start.startsWith(INCLUSIVE_PREFIX)
                && !start.startsWith(EXCLUSIVE_PREFIX)) {
            throw new RuntimeException("Valid start must start with '" + INCLUSIVE_PREFIX + "' or '"
                    + EXCLUSIVE_PREFIX + "'");
        }

        final Predicate<Slice> compareToStart = p -> START_UNBOUNDED.equals(start) ||
                (start.startsWith(INCLUSIVE_PREFIX)
                        ? p.toString().compareTo(start.substring(1)) >= 0
                        : p.toString().compareTo(start.substring(1)) > 0);

        String end = params().get(2).toString();
        if (!END_UNBOUNDED.equals(end)
                && !end.startsWith(INCLUSIVE_PREFIX)
                && !end.startsWith(EXCLUSIVE_PREFIX)) {
            throw new RuntimeException("Valid end must start with '" + INCLUSIVE_PREFIX + "' or '"
                    + EXCLUSIVE_PREFIX + "'");
        }

        final Predicate<Slice> compareToEnd = p -> END_UNBOUNDED.equals(end) ||
                (end.startsWith(INCLUSIVE_PREFIX)
                        ? p.toString().compareTo(end.substring(1)) <= 0
                        : p.toString().compareTo(end.substring(1)) < 0);

        List<Slice> values = map.keySet().stream()
                .filter(compareToStart.and(compareToEnd))
                .sorted()
                .map(Response::bulkString)
                .collect(Collectors.toList());

        return Response.array(values);
    }
}
