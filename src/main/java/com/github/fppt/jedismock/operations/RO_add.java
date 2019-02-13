package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.RedisBase;

import java.util.Collection;
import java.util.List;

import static com.github.fppt.jedismock.Utils.serializeObject;

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
            base().putValue(key, serializeObject(collection));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Response.integer(collection.size());
    }
}
