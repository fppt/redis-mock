package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.fppt.jedismock.Utils.deserializeObject;

public class RO_sinter extends AbstractRedisOperation {
    public RO_sinter(RedisBase base, List<Slice> params) {
        super(base, params,  null, null, null);
    }

    @Override
    Slice response() {
        Slice key = params().get(0);
        Set<Slice> resultSoFar = getSet(key);

        for(int i = 1; i < params().size(); i++){
            Set<Slice> set = getSet(params().get(i));
            resultSoFar = Sets.intersection(resultSoFar, set);
        }

        ImmutableList.Builder<Slice> builder = new ImmutableList.Builder<Slice>();
        resultSoFar.forEach(element -> builder.add(Response.bulkString(element)));

        return Response.array(builder.build());
    }

    private Set<Slice> getSet(Slice key){
        Set<Slice> set;
        Slice data = base().getValue(key);
        if (data != null) {
            set = new HashSet<>(deserializeObject(data));
        } else {
            set = new HashSet<>();
        }
        return set;
    }
}
