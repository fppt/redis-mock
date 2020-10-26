package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.fppt.jedismock.Utils.convertToDouble;
import static com.github.fppt.jedismock.Utils.serializeObject;


public class RO_zremrangebyscore extends AbstractRedisOperation {

    private static final String LOWEST_POSSIBLE_SCORE = "-inf";
    private static final String HIGHEST_POSSIBLE_SCORE = "+inf";
    private static final String EXCLUSIVE_PREFIX = "(";

    RO_zremrangebyscore(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        final Slice key = params().get(0);
        final LinkedHashMap<Slice, Double> map = getDataFromBase(key, new LinkedHashMap<>());

        if (map == null || map.isEmpty()) return Response.integer(0);

        final String start = params().get(1).toString();
        final double startScore = getStartScore(start);


        final Predicate<Double> compareToStart = p -> start.startsWith(EXCLUSIVE_PREFIX)
                ? p.compareTo(startScore) > 0
                : p.compareTo(startScore) >= 0;

        final String end = params().get(2).toString();
        final double endScore = getEndScore(end);

        final Predicate<Double> compareToEnd = p -> (end.startsWith(EXCLUSIVE_PREFIX)
                ? p.compareTo(endScore) < 0
                : p.compareTo(endScore) <= 0);

        List<Double> values = map.values().stream()
                .filter(compareToStart.and(compareToEnd))
                .collect(Collectors.toList());

        final Map<Slice, Double> result = map.entrySet().stream()
                .filter(entry -> compareToStart.and(compareToEnd).negate().test(entry.getValue()) )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        try {
            base().putValue(key, serializeObject(result));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return Response.integer(values.size());
    }

    private double getStartScore(String start) {
        if (LOWEST_POSSIBLE_SCORE.equals(start)) {
            return Double.MIN_VALUE;
        } else if(start.startsWith(EXCLUSIVE_PREFIX)) {
            return convertToDouble(start.substring(1));
        } else if (Character.isDigit(start.charAt(0))) {
            return convertToDouble(start);
        } else {
            throw new RuntimeException("Valid start must be a number or start with '" + EXCLUSIVE_PREFIX + "' or be equal to '"
                    + LOWEST_POSSIBLE_SCORE + "'");
        }
    }

    private double getEndScore(String end) {
        if (HIGHEST_POSSIBLE_SCORE.equals(end)) {
            return Double.MAX_VALUE;
        } else if(end.startsWith(EXCLUSIVE_PREFIX)) {
            return convertToDouble(end.substring(1));
        } else if (Character.isDigit(end.charAt(0))) {
            return convertToDouble(end);
        } else {
            throw new RuntimeException("Valid end must be a number or start with '" + EXCLUSIVE_PREFIX + "' or be equal to '"
                    + HIGHEST_POSSIBLE_SCORE + "'");
        }
    }
}
