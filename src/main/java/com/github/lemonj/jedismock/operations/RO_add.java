package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.Utils;

import java.util.Collection;
import java.util.List;

abstract class RO_add<V extends Collection<Slice>> extends AbstractRedisOperation {
    RO_add(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    abstract void addSliceToCollection(V collection, Slice slice);

    abstract V getDefaultResponse();

    Slice response() {
        Slice key = params().get(0);
        V collection = getDataFromBase(key, getDefaultResponse());

        for (int i = 1; i < params().size(); i++) {
            addSliceToCollection(collection, params().get(i));
        }

        try {
            base().putValue(key, Utils.serializeObject(collection));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Response.integer(collection.size());
    }
}
