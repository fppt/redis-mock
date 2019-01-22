package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.Slice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RO_sadd extends RO_add<Set<Slice>> {
    RO_sadd(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    void addSliceToCollection(Set<Slice> set, Slice slice) {
        for (int i = 1; i < params().size(); i++) {
            set.add(params().get(i));
        }
    }

    @Override
    Set<Slice> getDefaultResponse() {
        return new HashSet<>();
    }
}
